(ns starcity-build.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd.steps.git :as git]))

(defn some-step-that-does-nothing [args ctx]
  {:status :success})

(defn some-step-that-echos-foo [args ctx]
  (shell/bash ctx "/" "echo foo"))

(defn some-step-that-echos-bar [args ctx]
  (shell/bash ctx "/" "echo bar"))

(defn some-failing-step [args ctx]
  (shell/bash ctx "/" "echo \"i am going to fail now...\"" "exit 1"))

(def repo-uri "https://github.com/jalehman/starcity-web.git")
(def repo-branch "master")

(defn wait-for-repo [args ctx]
  (git/wait-for-git ctx repo-uri repo-branch))

(defn with-repo [& steps]
  (git/with-git repo-uri steps))

(defn build-project [args ctx]
  (shell/bash ctx (:cwd args) "lein with-profile production init-script"))
