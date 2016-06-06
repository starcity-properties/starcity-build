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
  (shell/bash ctx (:cwd args) "lein uberjar"))

(defn install-jar [args ctx]
  (let [project    (read-string (slurp "project.clj"))
        name       (str (second project))
        version    (nth project 2)
        install-to (str (System/getenv "WEBSERVER_INSTALL_DIR")
                        "/"
                        (System/getenv "WEBSERVER_BINARY_NAME"))]
    (shell/bash ctx (:cwd args) (format "cp target/%s-%s-standalone.jar %s"
                                        name version install-to))))

(defn restart-service [args ctx]
  (let [service (System/getenv "WEBSERVER_SERVICE_NAME")]
    (shell/bash ctx (:cwd args) (format "sudo systemctl restart %s" service))))
