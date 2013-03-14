(defproject jawaab "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/java.jdbc "0.1.4"]
                 [clj-time "0.4.4"]
                 [clojureql "1.0.3"]
                 [mysql/mysql-connector-java "5.1.18"]]

  :plugins [[lein-ring "0.8.3"]]
  :ring
    {:handler jawaab.server/app
     :port 24014}
  :main jawaab.server)

