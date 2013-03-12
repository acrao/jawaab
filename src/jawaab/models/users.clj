(ns jawaab.models.users
  (:use
    jawaab.models.common)
  (:require
    [clojure.java.jdbc :as jdbc]
    [noir.util.crypt :as crypt]
    [clojureql.core :as cql]))

(defn exists?
  "Searches for existing user"
  [email]
  (not (empty? (-> (cql/table db-spec :users)
                 (cql/select (cql/where (= :email email))) deref))))

(defn create!
  "Creates a new user record"
  [user]
  (->> (assoc user :password (crypt/encrypt (:password user)))
    (jdbc/insert-record :users)
    (jdbc/with-connection db-spec)
    :generated_key))

(defn lookup
  "Lookup an existing user by email address"
  [email]
  (-> (cql/table db-spec :users)
    (cql/select (cql/where (= :email email)))
    deref))

(defn authenticate
  [email password]
  (when-let [[result] (lookup email)]
    (when (crypt/compare password (:password result))
      (:id result))))