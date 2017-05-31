(defproject build "0.2.0-SNAPSHOT"
  :description "Starcity's build server."
  :url "https://github.com/starcity-properties/starcity-build.git"
  :dependencies [[lambdacd "0.9.3"]
                 [ring-server "0.3.1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-core "1.0.13"]
                 [ch.qos.logback/logback-classic "1.0.13"]
                 [ring-basic-authentication "1.0.5"]]

  :profiles {:uberjar {:aot :all}}

  :main build.core)
