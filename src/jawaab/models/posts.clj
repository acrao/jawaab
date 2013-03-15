(ns jawaab.models.posts
  (:use
    jawaab.models.common)
  (:require
    [clojure.string :as s]
    [clojure.java.jdbc :as jdbc]
    [clojureql.core :as cql]))

(defn create
  "Insert a new post record into the database. Return the auto generate post id"
  [post]
  (let [p (if (s/blank? (:parent_id post)) (dissoc post :parent_id) post)]
    (->> (assoc p :stime (local-time))
      (jdbc/insert-record :posts)
      (jdbc/with-connection db-spec)
      :generated_key)))

(defn update!
  "Update an existing post record specified by :id"
  [{:keys [id body]}]
  (-> (cql/table db-spec :posts)
    (cql/update-in! (cql/where (= :id id)) {:body body})))

(defn delete!
  "Delete a post specified by :id"
  [post-id]
  (-> (cql/table db-spec :posts)
    (cql/disj! (cql/where (= :id post-id)))))

(defn get-post
  "Get post by id"
  [post-id]
  (-> (cql/table db-spec :posts)
    (cql/select (cql/where (= :id post-id)))
    deref first))

(defn get-posts-for-parent
  "Returns a tuple of of parent post and all posts associated with the parent
   post sorted by number of votes/post"
  [parent-id]
  (let [parent-post (get-post parent-id)
        votes-by-posts (-> (cql/table db-spec :post_votes)
                         (cql/aggregate [[:sum/val :as :votes]] [:post_id]))
        replies (-> (cql/table db-spec :posts)
                  (cql/outer-join votes-by-posts :left (cql/where (= :id :post_id)))
                  (cql/select (cql/where (= :parent_id parent-id)))
                  (cql/sort [:post_votes_subselect.votes#desc :stime#asc])
                  deref)]
    [parent-post replies]))

(defn get-latest-posts
  [num]
  "Gets latest num posts sorted by submission time"
  (-> (cql/table db-spec :posts)
    (cql/select (cql/where (= :type "q")))
    (cql/sort [:stime#desc])
    (cql/take num)
    deref))

(defn lookup-vote
  [post-id uid direction]
  (-> (cql/table db-spec :post_votes)
    (cql/select
      (cql/where (and (= :post_id post-id) (= :user_id uid) (= :val direction))))
    deref))

(defn voted?
  [post-id uid direction]
  (when-let [result (first (lookup-vote post-id uid direction))]
    true))

(defn vote!
  "Registers a vote for a post"
  [post-id uid direction]
  (-> (cql/table db-spec :post_votes)
    (cql/conj! {:post_id post-id :user_id uid :val direction})))

(defn count-votes
  "Count the number of votes for a post id"
  [post-id]
  (-> (cql/table db-spec :post_votes)
    (cql/select (cql/where (= :post_id post-id)))
    (cql/aggregate [:sum/val :as :votes])
    (cql/pick :votes) deref))