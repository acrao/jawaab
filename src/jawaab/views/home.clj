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
  [:tr
   [:td stime]
   [:td (elem/link-to (url-for "/post/:id/view" {:id id}) title)]
   [:td (or (posts/num-answers id) 0)]])

(defpage "/" []
  (layout (login nil)))

(defpage "/home" []
  (layout
    [:div.row
      [:div.posts-list.span12
        [:table.table-striped.span12
          [:thead.align-right
            [:th "Date"]
            [:th "Title"]
            [:th "Answers"]]
          [:tbody
            (map home-posts (posts/get-latest-posts 20))]]]]))