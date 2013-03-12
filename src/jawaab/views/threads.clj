(ns jawaab.views.threads
  (:use
    [noir.core :only [defpartial defpage render]]
    jawaab.views.common
    )
  (:require
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

(defpartial thread
  [posts]
  [:div.container-fluid
    (map post posts)])

(defpage "/thread/:id" {thread-id :id}
  (layout
    (thread (posts/posts-for-thread thread-id))))