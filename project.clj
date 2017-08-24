(defproject build "0.2.0"
  :description "Starcity's build server."
  :url "https://github.com/starcity-properties/starcity-build.git"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 ;; Logging
                 [org.clojure/tools.logging "0.3.0"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-core "1.0.13"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [org.clojure/tools.cli "0.3.5"]
                 [lambdacd "0.13.1"]
                 [aero "1.1.2"]
                 [mount "0.1.11"]
                 [http-kit "2.2.0"]
                 [ring-basic-authentication "1.0.5"]
                 [hiccup "1.0.5"]]

  :profiles {:dev     {:source-paths ["src" "env/dev"]}
             :uberjar {:aot :all}}

  :repl-options {:init-ns user}

  :main build.core)
