(ns starcity-build.pipeline
  (:use [lambdacd.steps.control-flow])
  (:require [lambdacd.steps.manualtrigger :refer [wait-for-manual-trigger]]
            [starcity-build.steps.web :as web]
            [starcity-build.steps.build :as build]))

(def web-pipeline-def
  `(
    (either
     wait-for-manual-trigger
     web/wait-for-repo)
    (web/with-repo
      web/build-project
      web/install-scripts
      web/restart-service)))

;; (def build-pipeline-def
;;   `(
;;     (either
;;      wait-for-manual-trigger
;;      build/wait-for-repo)
;;     (build/with-repo
;;       build/build-project
;;       build/install-scripts
;;       build/restart-service)))
