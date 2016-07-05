(ns starcity-build.pipeline
  (:use [lambdacd.steps.control-flow :refer :all])
  (:require [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]
            [starcity-build.steps :as steps]))

(def pipeline-def
  `(
    (either
     (either
      wait-for-manual-trigger
      steps/wait-for-production-repo)
     (either
      wait-for-manual-trigger
      steps/wait-for-staging-repo))
    (steps/with-repo
      steps/build-project
      steps/install-jar)))
