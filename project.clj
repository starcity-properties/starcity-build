(defproject starcity-build "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[lambdacd "0.6.0"]
                 [ring-server "0.3.1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.slf4j/slf4j-api "1.7.5"]
                 [ch.qos.logback/logback-core "1.0.13"]
                 [ch.qos.logback/logback-classic "1.0.13"]]

  :plugins [[org.clojars.strongh/lein-init-script "1.3.1"]]

  :lis-opts {:redirect-output-to "/var/log/starcity-build-init.log"
             :name               "sbuild"
             :jvm-opts           ["-Xms128m"
                                  "-Xmx512m"
                                  "-server"]}

  :profiles {:uberjar {:aot :all}}
  :main starcity-build.core)
