(ns jawaab.views.common
  (:use
    [noir.core :only [defpartial url-for]]
    [hiccup.page :only [include-css html5 include-js]])
  (:require
    [hiccup.element :as elem]
    [hiccup.form :as form]))

(def ^:private header
  "Jawaab Header HTML"
  [:header.row
   [:div.navbar.navbar-static-top
     [:div.navbar-inner
       (elem/link-to {:class "brand"} "/" "Jawaab")
       [:div.container
         (elem/unordered-list {:class "nav"}
           [(elem/link-to (url-for "/home") "Home")
            (elem/link-to (url-for "/new_post_form") "New")
            ; TODO Sessions and sign out support
            (elem/link-to (url-for "/") "Sign out")])
           (form/form-to {:class "navbar-search pull-left"} [:get "/search"]
             (form/text-field {:class "search-query" :placeholder "Search"}
               "search"))]]]])

(defpartial layout [& content]
  (html5
    [:head
     [:title "Jawaab"]
     (include-css "/css/bootstrap.min.css")]
   (include-js "http://code.jquery.com/jquery-latest.js")
    [:body
     [:div.container
      [:div.page-header header]
      [:section.main.row
        [:div.span12.row content]]]]))