(ns jawaab.models.threads
  (:use
    jawaab.models.common)
  (:require
    [clojureql.core :as cql]))

(defn create!
  "Insert a new thread record into the database"
  [thread]
  (-> (cql/table db-spec :threads)
    (cql/conj! (assoc thread :stime (local-time)))))

(defn update!
  "Update an existing thread record specified by :id"
  [{:keys [id title]}]
  (-> (cql/table db-spec :threads)
    (cql/update-in! (cql/where (= :id id)) {:title title})))

(defn delete!
  "Delete a thread specified by :id"
  [{:keys [id]}]
  (-> (cql/table db-spec :threads)
    (cql/disj! (cql/where (= :id id)))))

(defn get-latest-threads
  []
  ; Mocked
  (for [i (range 0 10)]
    {:id i :title "Mytitle" :stime i}))