(ns jawaab.views.posts
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [noir.response :only [redirect]]
    [hiccup.page :only [include-js]]
    jawaab.views.common
    )
  (:require
    [hiccup.form :as form]
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]))

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
          [:button.btn.btn-success "U"]
          [:button.btn.btn-failure "D"]]]
      [:div
       [:p 5]]]
    [:div
      (:body post)]]
  [:div.row-fluid
   [:p [:bold "Tags : "]
    ; TODO -> Get tags for post
    "Tag123"]]
  [:div.row-fluid
    [:div.span3.offset6
      [:small (format "Submitted by %s" (:user_id post))]]]
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

(defpage "/post/new" []
  (layout (new-post-form nil false)))

(defpage [:post "/post/create"] post
  (let [post_id (posts/create (dissoc post :tags))
        post (posts/get-post post_id)]
    (redirect (url-for "/post/:id/view" {:id (or (:parent_id post) post_id)}))))

(defpage "/post/:id/view" {post-id :id}
  (layout
    (post-layout (posts/get-posts-for-parent post-id))))