(ns jawaab.views.home
  (:use
    [noir.core :only [defpartial defpage url-for]]
    jawaab.views.common)
  (:require
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]))

(defpartial home-posts
  [{:keys [id title stime]}]
  [:tr
   [:td id]
   [:td (elem/link-to (url-for "/post/:id/view" {:id id}) title)]
   [:td stime]])

(defpage "/" []
  (layout (login nil)))

(defpage "/home" []
  (layout
    [:div#posts
      [:table
        [:tbody
          (map home-posts (posts/get-latest-posts 20))]]]))