(ns build.core
  (:gen-class)
  (:require [build
             [config :as config :refer [config]]
             [pipeline :as pipeline]]
            [compojure.core :as compojure]
            [clojure.tools
             [cli :as cli]
             [logging :as log]]
            [lambdacd
             [core :as lambdacd]
             [runners :as runners]]
            [lambdacd.ui.core :as ui]
            [mount.core :as mount :refer [defstate]]
            [org.httpkit.server :as httpkit]
            [ring.middleware.basic-authentication
             :refer
             [wrap-basic-authentication]]))

;; =============================================================================
;; Server
;; =============================================================================

(defn authenticated? [user pass]
    (and (= user (config/auth-user config))
         (= pass (config/auth-pass config))))

(defn- handler [routes]
  (-> routes
      (wrap-basic-authentication authenticated?)))

(defn- start-server [port projects]
  (let [pipelines (pipeline/assemble-pipelines projects)
        routes    (apply compojure/routes
                         (conj pipelines (compojure/GET "/" [] (pipeline/make-index projects))))]
    (httpkit/run-server (handler routes) {:port (Integer. port)})))

(defstate server
  :start (do
           (log/info "Starting server...")
           (start-server (config/webserver-port config)
                         (config/projects config)))
  :stop (do
          (log/info "Stopping server...")
          (server)))

;; =============================================================================
;; Entry
;; =============================================================================

(def ^:private cli-options
  [["-e" "--environment ENVIRONMENT" "The environment to start the server in."
    :id :env
    :default :prod
    :parse-fn keyword
    :validate [#{:prod :dev} "Must be one of #{:prod, :dev}"]]])

(defn- exit [status msg]
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options errors]} (cli/parse-opts args cli-options)]
    (when errors
      (exit 1 (clojure.string/join "\n" errors)))
    (mount/start-with-args {:env (:env options)})))
