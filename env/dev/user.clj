(ns user
  (:require [build.config]
            [build.core]
            [clojure.tools.namespace.repl :refer [refresh]]
            [mount.core :as mount :refer [defstate]]))


(def start #(mount/start-with-args {:env :dev}))

(def stop mount/stop)

(defn go []
  (start)
  :ready)

(defn reset []
  (stop)
  (refresh :after 'user/go))
