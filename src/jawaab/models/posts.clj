(ns jawaab.models.posts
  (:use
    jawaab.models.common)
  (:require
    [clojureql.core :as cql]))

(defn create!
  "Insert a new post record into the database"
  [post]
  (-> (cql/table db-spec :posts)
    (cql/conj! (assoc post :stime (local-time)))))

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