(ns build.steps
  (:require [build.config :as config :refer [config]]
            [clojure.string :as string]
            [lambdacd.steps.shell :as shell]
            [lambdacd-git.core :as git]))


;; =============================================================================
;; Steps
;; =============================================================================


(defn wait-for-repo
  [repo-uri branch]
  (fn [args ctx]
    (git/wait-for-git ctx repo-uri :ref (format "refs/heads/%s" branch))))


(defn clone
  [repo-uri]
  (fn [args ctx]
   (git/clone ctx repo-uri (:revision args) (:cwd args))))


(defn build
  [script]
  (fn [args ctx]
    (shell/bash ctx (:cwd args) script)))


(defn project-meta [args]
  (let [project (-> (format "%s/project.clj" (:cwd args)) slurp read-string)]
    [(-> (second project) str (string/split #"/") last)
     (nth project 2)]))


(defn install-jar
  [deploy-to install-dir]
  (fn [args ctx]
    (let [[project-name version] (project-meta args)]
     (shell/bash ctx (:cwd args)
                 (format "scp target/%s-%s-standalone.jar %s@%s:%s/%s.jar"
                         project-name
                         version
                         (config/deploy-user config)
                         deploy-to
                         install-dir
                         project-name)))))


(defn restart-service
  [deploy-to]
  (fn [args ctx]
    (let [[project-name _] (project-meta args)]
     (shell/bash ctx (:cwd args)
                 (format "ssh %s@%s 'sudo systemctl restart %s.service'"
                         (config/deploy-user config)
                         deploy-to
                         project-name)))))
