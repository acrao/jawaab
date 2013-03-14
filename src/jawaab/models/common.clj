(ns jawaab.models.common
  (:require
    [clj-time.local :as localtime])
  (:import java.util.UUID))

(defn format-now
  [formatter]
  "Returns a formatted local date time string"
  (localtime/format-local-time (localtime/local-now) formatter))

(def local-time #(format-now :date-hour-minute-second))

(defn generate-hash
  []
  "Generates a random hash using java.utils.UUID and cuttent time"
  (format "%s-%s" (format-now :basic-ordinal-date-time)
    (subs (str (UUID/randomUUID)) 0 8)))

(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/jawaab"
   :user "jawaab_app"
   :password "prashan"})