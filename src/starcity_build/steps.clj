(ns starcity-build.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]))

;; =============================================================================
;; Helpers
;; =============================================================================

(def ^:private repo-uri "https://github.com/starcity-properties/starcity-web.git")

(defn- wait-for-repo
  [branch]
  (let [build (if (= branch "master") :production :staging)]
    (fn [args ctx]
      (let [step (git/wait-for-git ctx repo-uri branch)]
        (merge step {:global {:build build}
                     :details [{:label (str "Building: " build)}]})))))

(def ^:private configs
  {:production {:install-dir "/var"
                :name        "starcity-web"
                :user        "deploy"
                :domain      "192.168.137.39"}
   :staging    {:install-dir "/var"
                :name        "starcity-web"
                :user        "deploy"
                :domain      "192.168.142.43"}})

;; =============================================================================
;; API
;; =============================================================================

(def wait-for-production-repo (wait-for-repo "master"))
(def wait-for-staging-repo (wait-for-repo "development"))

(defn with-repo [& steps]
  (git/with-git repo-uri steps))

(defn build-project [args ctx]
  (shell/bash ctx (:cwd args) "bower install && lein uberjar"))

(defn install-jar [args ctx]
  (let [project                                (-> (format "%s/project.clj" (:cwd args)) slurp read-string)
        project-name                           (str (second project))
        version                                (nth project 2)
        build                                  (get-in args [:global :build])
        {:keys [install-dir name user domain]} (get configs build)]
    (shell/bash ctx (:cwd args)
                (format "scp target/%s-%s-standalone.jar %s@%s:%s/%s/%s.jar"
                        project-name version
                        user domain install-dir name name))))

(defn restart-service [args ctx]
  (let [build (get-in args [:global :build])
        {:keys [name user domain]} (get configs build)]
    (shell/bash ctx (:cwd args)
                (format "ssh %s@%s 'sudo systemctl restart %s.service'"
                        user domain name))))
