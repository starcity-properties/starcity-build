(ns starcity-build.pipeline
  (:use [lambdacd.steps.control-flow]
        [starcity-build.steps])
  (:require [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]))

(def pipeline-def
  `(
    (either
     wait-for-manual-trigger
     wait-for-repo)
    (with-repo
      build-project
      install-scripts
      restart-service)))
