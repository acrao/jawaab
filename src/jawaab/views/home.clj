(ns jawaab.views.home
  (:use
    [noir.core :only [defpartial defpage url-for]]
    jawaab.views.common)
  (:require
    [jawaab.models.users :as users]
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]))

(defpartial home-posts
  [{:keys [id title stime]}]
  [:div.row
    [:div.row
      [:div.span7 [:strong (elem/link-to (url-for "/post/:id/view" {:id id}) title)]]
      [:div.span2 [:p stime]]
      [:div.span1 [:p (posts/num-answers id)]]]
    [:div.row
      "Tags : Tag1 Tag1 Tag1 Tag1 Tag1 Tag1 Tag1 Tag1"]
    [:hr]])

(defpage "/" []
  (layout (login nil)))

(defpage "/home" []
  (layout
    [:div.row
      (elem/unordered-list {:class "home-list"}
        (map home-posts (posts/get-latest-posts 20)))]))