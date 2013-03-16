(ns jawaab.views.posts
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [hiccup.page :only [include-js]]
    jawaab.views.common
    )
  (:require
    [hiccup.form :as form]
    [hiccup.element :as elem]
    [noir.response :as response]
    [jawaab.models.posts :as posts]
    [jawaab.models.users :as users]))

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
      [:div.span2
        (form/form-to [:put "/post/delete"]
          (form/hidden-field "id" (:id post))
          [:button {:type "submit"} [:i.icon-remove-circle.icon-3x]])]]
    [:div.row
      [:div.span9
        [:p.text-left [:bold "Tags : "]
          ; TODO -> Get tags for post
        "Tag123 Tag123 Tag123 Tag123 Tag123 Tag123 Tag123 Tag123 Tag123 Tag123"]]
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

(defpartial post-layout
  [[parent-post replies]]
  (format-post parent-post true)
  (let [c (count replies)]
    [:h4 (format "%s Answer%s" c (if (> c 1) "s" ""))])
  [:hr]
  (map #(format-post % false) replies)
;  [:div.row
;    [:button.reply-post.btn.btn-primary "Reply"]]
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

(defpage [:post "/post/create"] post
  (let [post_id (posts/create (dissoc post :tags))
        post (posts/get-post post_id)]
    (response/redirect
      (url-for "/post/:id/view" {:id (or (:parent_id post) post_id)}))))

(defpage [:put "/post/delete"] {post-id :id}
  (let [post (posts/get-post post-id)]
    (posts/delete! post-id)
    (response/redirect (url-for "/post/:id/view" {:id (:parent_id post)}))))

(defpage "/post/:id/view" {post-id :id}
  (layout
    (post-layout (posts/get-posts-for-parent post-id))))