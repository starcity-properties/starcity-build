(ns starcity-build.core
  (:require
   [starcity-build.pipeline :as pipeline]
   [ring.server.standalone :as ring-server]
   [lambdacd.ui.ui-server :as ui]
   [lambdacd.runners :as runners]
   [lambdacd.util :as util]
   [lambdacd.core :as lambdacd]
   [clojure.tools.logging :as log])
  (:gen-class))

(defn -main [& args]
  (let [;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        home-dir       "/usr/local/starcity-lambdacd"
        config         {:home-dir home-dir
                        :name     "starcity build"}
        ;; initialize and wire everything together
        web-pipeline   (lambdacd/assemble-pipeline
                        pipeline/web-pipeline-def
                        config)
        ;; build-pipeline (lambdacd/assemble-pipeline
        ;;                 pipeline/build-pipeline-def
        ;;                 config)
        ;; create a Ring handler for the UI
        app            (ui/ui-for web-pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another web-pipeline)
    ;; (runners/start-one-run-after-another build-pipeline)
    ;; start the webserver to serve the UI
    (ring-server/serve app {:open-browser? false
                            :port          8085})))
