(ns jawaab.models.users
  (:use
    jawaab.models.common)
  (:require
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
  (-> (cql/table db-spec :users)
    (cql/conj! (assoc user :password (crypt/encrypt (:password user))))))

(defn authenticate
  [email password]
  (when-let [result
              (-> (cql/table db-spec :users)
                (cql/project [:password])
                (cql/select (cql/where (= :email email)))
                deref first)]
    (crypt/compare password (:password result))))