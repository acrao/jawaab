(ns jawaab.models.tags
  (:use
    jawaab.models.common)
  (:require
    [clojure.java.jdbc :as jdbc]
    [clojureql.core :as cql]))

(defn create
  "Insert a new tag"
  [tag-name]
  (->> {:name tag-name}
    (jdbc/insert-record :tags)
    (jdbc/with-connection db-spec)
    :generated_key))

(defn lookup-tag
  "Lookup a tag by name"
  [tag-name]
  (-> (cql/table db-spec :tags)
    (cql/select (cql/where (= :name tag-name)))
    deref first :id))

(defn tags-by-post
  "Get all tags associated with a post id"
  []
  ;TODO
  )

(defn tag-post
  "Add a tag to a post"
  [post-id tag-name]
  (let [tag-id (or (lookup-tag tag-name) (create tag-name))]
    (-> (cql/table db-spec :tags)
      (cql/conj! {:post_id post-id :tag_id tag-id}))))