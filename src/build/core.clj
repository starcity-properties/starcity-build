(ns build.core
  (:gen-class)
  (:require [build.pipeline :as pipeline]
            [ring.server.standalone :as ring-server]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [lambdacd.ui.ui-server :as ui]
            [lambdacd.runners :as runners]
            [lambdacd.util :as util]
            [lambdacd.core :as lambdacd]
            [clojure.tools.logging :as log]))

#_(defn authenticated? [name pass]
  (and (= name (System/getenv "USER"))
       (= pass (System/getenv "PASSWORD"))))

#_(defn -main [& args]
  (let [;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        data-dir (System/getenv "DATA_DIR")
        config   {:home-dir data-dir
                  :name     "starcity build"}
        ;; initialize and wire everything together
        pipeline (lambdacd/assemble-pipeline
                  pipeline/pipeline-def
                  config)
        ;; create a Ring handler for the UI
        app      (ui/ui-for pipeline)]
    (log/info "LambdaCD Data Directory is " data-dir)
    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another pipeline)
    ;; start the webserver to serve the UI
    (ring-server/serve (wrap-basic-authentication app authenticated?)
                       {:open-browser? false
                        :port          8085})))
