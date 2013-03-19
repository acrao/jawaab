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

(defn get-tag
  "Get tag by tag id"
  [tag-id]
  (-> (cql/table db-spec :tags)
    (cql/select (cql/where (= :id tag-id)))
    deref first))

(defn lookup-name
  "Lookup a tag by name"
  [tag-name]
  (-> (cql/table db-spec :tags)
    (cql/select (cql/where (= :name tag-name)))
    deref first))

(defn tags-by-post
  "Get all tags associated with a post id"
  [post-id]
  (-> (cql/table db-spec :tags)
    (cql/join (cql/table db-spec :post_tags)
      (cql/where (= :post_tags.tag_id :tags.id)))
    (cql/select (cql/where (= :post_tags.post_id post-id)))
    deref))

(defn posts-by-tag
  "Get all posts associated with a tag id"
  [tag-id]
  (-> (cql/table db-spec :posts)
    (cql/join (cql/table db-spec :post_tags)
      (cql/where (= :post_tags.post_id :posts.id)))
    (cql/select (cql/where (= :post_tags.tag_id tag-id)))
    deref))

(defn tag-post
  "Add a tag to a post"
  [post-id tag-name]
  (do
    (let [tag-id (or (:id (lookup-name tag-name)) (create tag-name))]
      (-> (cql/table db-spec :post_tags)
        (cql/conj! {:post_id post-id :tag_id tag-id})))))