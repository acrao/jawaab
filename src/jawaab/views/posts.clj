(ns jawaab.views.posts
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [hiccup.page :only [include-js]]
    jawaab.views.common)
  (:require
    [clojure.string :as s]
    [hiccup.form :as form]
    [hiccup.element :as elem]
    [noir.response :as response]
    [jawaab.models.posts :as posts]
    [jawaab.models.users :as users]
    [jawaab.models.tags :as tags]))

(defpartial format-post
  [post title?]
  [:div.row
    (when title?
      [:div.row
        [:h3 (:title post)]
        [:hr]])
    [:div.row.post-body
      [:div.span1
        [:div.row
          [:i
            {:class "icon-upload vote-button icon-3x"
             :data-post-id (:id post) :data-dir 1}]]
        [:div {:class (format "row vote-pad vote-count-%s" (:id post))}
          [:h6 (or (posts/count-votes (:id post)) 0)]]
        [:div.row
         [:i
           {:class "icon-download vote-button icon-3x"
            :data-post-id (:id post) :data-dir -1}]]]
      [:div.span9 [:p.text-left.post-text (:body post)]]
      (when-let [uid (users/get-uid)]
        [:div.span2
          [:i {:class "icon-remove-circle icon-2x delete-post"
               :data-post-id (:id post)
               :style (format "display:%s;"
                        (if (= (:user_id post) (Integer. (users/get-uid)))
                          "true"
                          "none"))}]])]
    [:div.row
      [:div.span9
        (format-tags (tags/tags-by-post (:id post)))]
      [:div.span3
        [:div.row
          [:p.pull-left
            (format "Submitted by %s" (->> post :user_id (users/lookup-id) :handle))]]
        [:div.row
          [:p (format "Submitted on %s" (:stime post))]]]]
    [:hr]])

(defpartial new-post-form
  [parent-id no-title?]
  (form/form-to {:class "new-post-form"}
    [:post "/post/create"]
    (when (not no-title?)
      [:div.control-group
        (form/label {:class "control-label"} "title" "Title")
        [:div.controls
          (form/text-area "title")]])
    [:div.control-group
      (form/label {:class "control-label"} "body" (if no-title? "Answer" "Question"))
      [:div.controls
        (form/text-area {:class "reply-text"} "body")]]
    [:div.control-group
      (form/label {:class "control-label"} "tags" "Tags")
      [:div.controls
        (form/text-field "tags")]]
    (form/hidden-field "user_id" (users/get-uid))
    (form/hidden-field "type" (if no-title? "a" "q"))
    (form/hidden-field "parent_id" parent-id)
    [:div.control-group
      [:div.controls
        (form/submit-button {:class "btn btn-primary"} "Post")]])
  [:div#preview])

(defn- handle-post-create
  [post]
  (let [post-id (posts/create (dissoc post :tags))
        tags (-> post :tags (s/split #",") (#(map s/trim %)))
        post (posts/get-post post-id)
        tag-ids (doall (map (fn [tag] (tags/tag-post post-id tag)) tags))]
    post))

(defpartial post-layout
  [[parent-post replies]]
  (format-post parent-post true)
  (let [c (count replies)]
    [:h4 (format "%s Answer%s" c (if (not (= c 1)) "s" ""))])
  [:hr]
  (map #(format-post % false) replies)
  [:div.row
    (new-post-form (:id parent-post) true)])

(defpage [post "/post/vote"] {:keys [id direction]}
  (let [uid (users/get-uid)
        float-direction (Float/valueOf direction)
        voted (posts/voted? id uid float-direction)]
    (if (and uid (not voted))
      (do
        (posts/vote! id uid float-direction)
        (response/json {:votes (posts/count-votes id)}))
      ; TODO figure out how to get a message on the page (sessions maybe)
      (response/redirect (url-for "/")))))

(defpage "/post/new" []
  (layout (new-post-form nil false)))

(defpage [:post "/post/create"] post-req
  (let [post (handle-post-create post-req)]
    (response/redirect
      (url-for "/post/:id/view" {:id (or (:parent_id post) (:id post))}))))

(defpage [:put "/post/delete"] {post-id :id}
  (let [post (posts/get-post post-id)]
    (posts/delete! post-id)
    (response/json
      {:next-url
         (cond
           (= (:type post) "q")
             (url-for "/home")
           :else
             (url-for "/post/:id/view" {:id (or (:parent_id post) post-id)}))})))

(defpage "/post/:id/view" {post-id :id}
  (layout
    (post-layout (posts/get-posts-for-parent post-id))))