(ns build.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]
            [build.config :as config :refer [config]]))


;; =============================================================================
;; Steps
;; =============================================================================


(defn wait-for-repo
  [repo-uri branch]
  (fn [args ctx]
    (git/wait-for-git ctx repo-uri branch)))


(defn with-repo
  [uri branch & steps]
  (git/with-git-branch uri branch steps))


(defn build
  [script]
  (fn [args ctx]
    (shell/bash ctx (:cwd args) script)))


(defn project-meta [args]
  (let [project (-> (format "%s/project.clj" (:cwd args)) slurp read-string)]
    [(str (second project)) (nth project 2)]))


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
