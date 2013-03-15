(ns jawaab.views.common
  (:use
    [noir.core :only [defpartial url-for]]
    [noir.response :only [redirect]]
    [hiccup.page :only [include-css html5 include-js]])
  (:require
    [hiccup.element :as elem]
    [hiccup.form :as form]
    [jawaab.models.posts :as posts]
    [jawaab.models.users :as users]))

(defpartial header
  []
   [:div.navbar.inverse.navbar-static-top
     [:div.navbar-inner
       [:div.container
         [:div.row
           [:div.span8
             (elem/link-to {:class "brand"} "/" "Jawaab")
             (elem/unordered-list {:class "nav"}
               [(elem/link-to (url-for "/home") "Home")
                (elem/link-to (url-for "/post/new") "New")
                ; TODO Sessions and sign out support
                (elem/link-to (url-for "/logout") "Sign out")])
               (form/form-to {:class "navbar-search pull-left"} [:get "/search"]
                 (form/text-field {:class "search-query" :placeholder "Search"}
                   "search"))]
           [:div.span4
             (when-let [uid (users/get-uid)]
               (elem/unordered-list {:class "nav pull-left"}
                 [[:p.muted (format "Signed in as %s"
                              (:name (users/lookup-id (Integer. uid))))]]))]]]]])

(defpartial layout
  [& content]
  (html5
    [:head
      [:title "Jawaab"]
      (include-css "/css/bootstrap.min.css")]
    (include-js "http://code.jquery.com/jquery-latest.js")
    (include-js "/js/posts.js")
    (header)
    (repeat 3 [:br])
    [:body
      [:div.container
        [:div.row
          [:div.span12 content]]]]))

(defpartial user-fields
  [signup?]
  [:div.control-group
    (form/label {:class "control-label"} "email" "Email")
    [:div.controls
      (form/email-field {:placeholder "Email"} "email")]]
  [:div.control-group
    (form/label {:class "control-label"} "password" "Password")
    [:div.controls
      (form/password-field {:placeholder "Password"} "password")]]
  (when signup?
    [:div
      [:div.control-group
        (form/label {:class "control-label"} "name" "Name")
        [:div.controls
          (form/text-field {:placeholder "Name"} "name")]]
      [:div.control-group
        (form/label {:class "control-label"} "handle" "Handle")
        [:div.controls
          (form/text-field {:placeholder "Handle"} "handle")]]]))

(defpartial login
  [error]
  [:strong "Login"]
  (when error
    [:div.alert.alert-error error])
  [:div#loginbox.hero-unit
    (form/form-to {:class "form-horizontal"} [:post "/login"]
       (user-fields false)
       [:div.control-group
         [:div.controls
           (form/submit-button {:class "btn btn-primary"} "Login")]])
    [:div#signup
      (form/form-to [:get "/signup"]
        (form/submit-button {:class "btn btn-info"} "Sign up"))]])

(defpartial signup
  []
  [:div#loginbox.hero-unit
    (form/form-to {:class "form-horizontal"} [:post "/user/new"]
      (user-fields true)
      [:div.control-group
        [:div.controls
          (form/submit-button {:class "btn btn-primary"} "Sign up")]])])