(ns starcity-build.steps.build
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]))

(def repo-uri "https://github.com/starcity-properties/starcity-build.git")
(def repo-branch "master")

(defn wait-for-repo [args ctx]
  (git/wait-for-git ctx repo-uri repo-branch))

(defn with-repo [& steps]
  (git/with-git repo-uri steps))

(defn build-project [args ctx]
  (shell/bash ctx (:cwd args)
              "lein init-script"))

(defn install-scripts [args ctx]
  (shell/bash ctx (:cwd args)
              "./init-script/clean-sbuild"
              "./init-script/install-sbuild"))

(defn restart-service [args ctx]
  (shell/bash ctx (:cwd args)
              "service sbuildd restart"))
