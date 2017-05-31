(ns build.pipeline
  (:use [lambdacd.steps.control-flow :refer :all])
  (:require [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]
            [build.steps :as steps]))

#_(def pipeline-def
  `(
    (either
     steps/wait-for-production-repo
     steps/wait-for-staging-repo)
    (steps/with-repo
      steps/build-assets
      steps/build-project
      steps/install-jar
      steps/restart-service)))
