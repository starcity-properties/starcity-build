(ns build.pipeline
  (:require [compojure.core :as compojure]
            [lambdacd.steps.manualtrigger :as manualtrigger]
            [lambdacd.steps.control-flow :as flow]
            [lambdacd.ui.core :as ui]
            [hiccup.core :as h]
            [build.config :as config :refer [config]]
            [build.steps :as steps]
            [lambdacd.core :as lambdacd]
            [lambdacd.runners :as runners]
            [lambdacd-git.core :as git])
  (:import (org.eclipse.jgit.transport UsernamePasswordCredentialsProvider)))

;; =============================================================================
;; Pipeline Index
;; =============================================================================


(defn- make-link [{:keys [pipeline-url name]}]
  [:li [:a {:href (str pipeline-url "/")} name]])


(defn make-index [projects]
  (h/html
   [:html
    [:head
     [:title "Pipelines"]]
    [:body
     [:h1 "Pipelines:"]
     [:ul (map make-link projects)]]]))


;; =============================================================================
;; Pipeline Generation
;; =============================================================================


(defn make-pipeline
  [{:keys [repo-uri repo-branch deploy-to install-dir build-script]}]
  `((flow/either
     manualtrigger/wait-for-manual-trigger
     (steps/wait-for-repo ~repo-uri ~repo-branch))
    (flow/with-workspace
      (steps/clone ~repo-uri)
      (steps/build ~build-script)
      (steps/install-jar ~deploy-to ~install-dir)
      (steps/restart-service ~deploy-to))))


(defn- pipeline-for [{:keys [data-dir name] :as project}]
  (let [conf      {:home-dir (str (config/deploy-dir config) "/" data-dir)
                   :name     name
                   :git      {:credentials-provider (UsernamePasswordCredentialsProvider.
                                                     (config/github-username config)
                                                     (config/github-username config))}
                   :ssh      {:use-agent                true
                              :known-hosts-files        ["~/.ssh/known_hosts"]
                              :strict-host-key-checking false}}
        pipeline  (lambdacd/assemble-pipeline (make-pipeline project) conf)
        ;; use this?
        ui-config {:expand-active-default   true
                   :expand-failures-default true}]
    (runners/start-one-run-after-another pipeline)
    (ui/ui-for pipeline)))


(defn- make-context [project]
  (let [app (pipeline-for project)]
    (compojure/context (:pipeline-url project) [] app)))


(defn assemble-pipelines [projects]
  (map make-context projects))
