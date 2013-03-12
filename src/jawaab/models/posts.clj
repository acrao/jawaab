(ns jawaab.models.posts
  (:use
    jawaab.models.common)
  (:require
    [clojure.java.jdbc :as jdbc]
    [clojureql.core :as cql]))

(defn create
  "Insert a new post record into the database. Return the auto generate post id"
  [post]
  (->> (assoc post :stime (local-time))
    (jdbc/insert-record :posts)
    (jdbc/with-connection db-spec)
    :generated_key))

(defn update!
  "Update an existing post record specified by :id"
  [{:keys [id body]}]
  (-> (cql/table db-spec :posts)
    (cql/update-in! (cql/where (= :id id)) {:body body})))

(defn delete!
  "Delete a post specified by :id"
  [{:keys [id]}]
  (-> (cql/table db-spec :posts)
    (cql/disj! (cql/where (= :id id)))))

(defn posts-for-thread
  [thread-id]
  (for [i (range 0 10)]
    {:id i
     :user_id "blah_user"
     :text "Help! I need somebody, Help!"
     :thread_id thread-id
     :type "q"
     :timestamp i}))

(defn get-latest-posts
  [num]
  "Gets latest num posts sorted by submission time"
  (-> (cql/table db-spec :posts)
    (cql/sort [:stime#desc])
    (cql/take num)
    deref))