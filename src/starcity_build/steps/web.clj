(ns starcity-build.steps.web
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]))

(def repo-uri "https://github.com/starcity-properties/starcity-web.git")
(def repo-branch "master")

(defn wait-for-repo [args ctx]
  (git/wait-for-git ctx repo-uri repo-branch))

(defn with-repo [& steps]
  (git/with-git repo-uri steps))

(defn build-project [args ctx]
  (shell/bash ctx (:cwd args)
              "lein with-profile production init-script"))

(defn install-scripts [args ctx]
  (shell/bash ctx (:cwd args)
              "./script/clean-starcity"
              "./script/install-starcity"))

(defn restart-service [args ctx]
  (shell/bash ctx (:cwd args)
              "service starcityd restart"))
