(ns jawaab.views.posts
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [noir.response :only [redirect]]
    jawaab.views.common
    )
  (:require
    [hiccup.form :as form]
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]))

(defpartial post
  [post]
  [:div.row-fluid
    [:div.container.span2
      [:div.container.span2
        [:div.row
          [:button.btn.btn-success "U"]
          [:button.btn.btn-failure "D"]]]
      [:div
       [:p 5]]]
    [:div
      (:text post)]]
  [:div.row-fluid
    [:div.span3.offset6
      [:small (format "Submitted by %s" (:user_id post))]]])

(defpartial new-post-form
  []
  (form/form-to {:class "form-horizontal"} [:post "/create_post"]
    [:div.control-group
      (form/label {:class "control-label"} "title" "Title")
      [:div.controls
        (form/text-area {:rows 4} "title")]]
    [:div.control-group
      (form/label {:class "control-label"} "body" "Question")
      [:div.controls
        (form/text-area {:rows 4} "body")]]
    [:div.control-group
      (form/label {:class "control-label"} "tags" "Tags")
      [:div.controls
        (form/text-field "tags")]]
    (form/hidden-field "type" "q")
    [:div.control-group
      [:div.controls
        (form/submit-button {:class "btn btn-primary"} "Post")]]))

(defpartial post-layout
  [posts]
  [:div.container-fluid
    (map post posts)])

(defpage "/new_post_form" []
  (layout (new-post-form)))

(defpage [:post "/create_post"] post
  (let [post_id (posts/create (dissoc post :tags))]
    (redirect (url-for "/post/:id" {:id post_id}))))

(defpage "/post/:id" {post-id :id}
  (layout
    (post-layout (posts/posts-for-thread post-id))))