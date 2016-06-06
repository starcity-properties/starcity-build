(ns starcity-build.pipeline
  (:use [lambdacd.steps.control-flow])
  (:require [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]
            [starcity-build.steps.web :as web]))

(def pipeline-def
  `(
    (either
     wait-for-manual-trigger
     web/wait-for-repo)
    (web/with-repo
      web/build-project
      web/install-jar
      web/restart-service)))
