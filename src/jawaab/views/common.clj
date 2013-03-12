(ns jawaab.views.common
  (:use
    [noir.core :only [defpartial]]
    [hiccup.page :only [include-css html5 include-js]])
  (:require
    [hiccup.element :as elem]))

(def ^:private header
  "Jawaab Header HTML"
  [:header.row
   [:div.span9.title
    (elem/link-to "/" [:h2 "Jawaab"])]])

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