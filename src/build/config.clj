(ns build.config
  (:require [aero.core :as aero]
            [mount.core :as mount :refer [defstate]]
            [clojure.java.io :as io]))

(defstate config
  :start (-> (io/resource "config.edn")
             (aero/read-config {:resolver aero/root-resolver
                                :profile  (:env (mount/args))})))

(defn webserver-port
  [config]
  (get-in config [:server :port]))

(defn projects
  [config]
  (:projects config))

(defn auth-user
  [config]
  (get-in config [:auth :user]))

(defn auth-pass
  [config]
  (get-in config [:auth :pass]))

(defn deploy-user
  [config]
  (get-in config [:deploy :user]))

(defn deploy-dir
  [config]
  (get-in config [:deploy :dir]))
