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
  [:div.row-fluid
    (when title?
      [:div
        [:h3 (:title post)]
        [:hr]])
    [:div.container.span2
      [:div.container.span2
        [:div.row
          [:button.vote-button.btn.btn-success
           {:data-post-id (:id post) :data-dir 1} "U"]
          [:button.vote-button.btn.btn-failure
           {:data-post-id (:id post) :data-dir -1}"D"]]]
      [:div {:class (format "vote-count-%s" (:id post))}
       [:p (or (posts/count-votes (:id post)) 0) ]]]
    [:div
      (:body post)]]
  [:div.row-fluid
   [:p [:bold "Tags : "]
    ; TODO -> Get tags for post
    "Tag123"]]
  [:div.row-fluid
    [:div.offset3
      (form/form-to [:put "/post/delete"]
        (form/hidden-field "id" (:id post))
        (form/submit-button {:class "btn btn-primary"} "Delete"))]
    [:div.span3.offset6
      [:small (format "Submitted by %s" (->> post :user_id (users/lookup-id) :handle))]]]
  [:hr])

(defpartial new-post-form
  [parent-id hidden?]
  (let [base-props {:class "form-horizontal new-post-form"}
        hidden-props {:style "display: none;"}]
    (form/form-to (if hidden? (merge base-props hidden-props) base-props)
      [:post "/post/create"]
      (when (not hidden?)
        [:div.control-group
          (form/label {:class "control-label"} "title" "Title")
          [:div.controls
            (form/text-area {:rows 4} "title")]])
      [:div.control-group
        (form/label {:class "control-label"} "body" (if hidden? "Answer" "Question"))
        [:div.controls
          (form/text-area {:rows 4} "body")]]
      [:div.control-group
        (form/label {:class "control-label"} "tags" "Tags")
        [:div.controls
          (form/text-field "tags")]]
      (form/hidden-field "user_id" (users/get-uid))
      (form/hidden-field "type" (if hidden? "a" "q"))
      (form/hidden-field "parent_id" parent-id)
      [:div.control-group
        [:div.controls
          (form/submit-button {:class "btn btn-primary"} "Post")]])))

(defpartial post-layout
  [[parent-post replies]]
  [:div.container-fluid
    (format-post parent-post true)
    (map #(format-post % false) replies)
    [:div.row-fluid
      [:button.reply-post.btn.btn-primary "Reply"]]
    [:div.row-fluid
      (new-post-form (:id parent-post) true)]])

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