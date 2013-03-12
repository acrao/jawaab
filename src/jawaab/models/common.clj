(ns jawaab.models.common
  (:require
    [clj-time.local :as localtime]))

(defn local-time
  []
  "Returns a formatted local date time string"
  (localtime/format-local-time (localtime/local-now) :date-hour-minute-second))

(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/jawaab"
   :user "jawaab_app"
   :password "prashan"})