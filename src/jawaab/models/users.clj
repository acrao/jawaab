(ns jawaab.models.users
  (:use
    jawaab.models.common)
  (:require
    [clojure.java.jdbc :as jdbc]
    [noir.util.crypt :as crypt]
    [clojureql.core :as cql]
    [noir.cookies :as cookies]))

(defn exists?
  "Searches for existing user"
  [email]
  (not (empty? (-> (cql/table db-spec :users)
                 (cql/select (cql/where (= :email email))) deref))))

(defn create
  "Creates a new user record"
  [user]
  (->> (assoc user :password (crypt/encrypt (:password user)))
    (jdbc/insert-record :users)
    (jdbc/with-connection db-spec)
    :generated_key))

(defn lookup-email
  "Lookup an existing user by email address"
  [email]
  (-> (cql/table db-spec :users)
    (cql/select (cql/where (= :email email)))
    deref))

(defn lookup-id
  "Get user by id"
  [id]
  (-> (cql/table db-spec :users)
    (cql/select (cql/where (= :id id)))
    deref first))

;; Authentication and cookie related methods

(defn authenticate
  [email password]
  (when-let [result (first (lookup-email email))]
    (when (crypt/compare password (:password result))
      (:id result))))

(defn save-uid!
  "Saves the user id using cookies and sessions"
  ([uid]
    (save-uid! uid (* 30 24 60 60 60)))
  ([uid max-age]
    (cookies/put! :uid
      {:value (str uid) :path "/" :max-age max-age})))

(defn get-uid
  "Gets the uid from the current cookie"
  []
  (cookies/get :uid))

(defn clear-cookies
  []
  (save-uid! -1 -1))