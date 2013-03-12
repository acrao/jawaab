(ns jawaab.views.home
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [noir.response :only [redirect]]
    jawaab.views.common)
  (:require
    [clojure.tools.logging :as log]
    [hiccup.form :as form]
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]
    [jawaab.models.users :as users]))

(defpartial login
  [error]
  [:strong "Login"]
  (when error
    [:div.alert.alert-error error])
  [:div#loginbox.hero-unit
    (form/form-to {:class "form-horizontal"} [:post "/login"]
      [:div.control-group
        (form/label {:class "control-label"} "email" "Email")
        [:div.controls
          (form/email-field {:placeholder "Email"} "email")]]
       [:div.control-group
         (form/label {:class "control-label"} "password" "Password")
         [:div.controls
           (form/password-field {:placeholder "Password"} "password")]]
       [:div.control-group
         [:div.controls
           (form/submit-button {:class "btn btn-primary"} "Login")]])
    [:div#signup
      (form/form-to [:get "/signup"]
        (form/submit-button {:class "btn btn-info"} "Sign up"))]])

(defpartial signup
  []
  [:div#loginbox.hero-unit
    (form/form-to {:class "form-horizontal"} [:post "/new_user"]
      [:div.control-group
        (form/label {:class "control-label"} "email" "Email")
        [:div.controls
          (form/email-field {:placeholder "Email"} "email")]]
       [:div.control-group
         (form/label {:class "control-label"} "password" "Password")
         [:div.controls
           (form/password-field {:placeholder "Password"} "password")]]
       [:div.control-group
         (form/label {:class "control-label"} "name" "Name")
         [:div.controls
           (form/password-field {:placeholder "Name"} "name")]]
       [:div.control-group
         (form/label {:class "control-label"} "handle" "Handle")
         [:div.controls
           (form/password-field {:placeholder "Handle"} "handle")]]
       [:div.control-group
         [:div.controls
           (form/submit-button {:class "btn btn-primary"} "Sign up")]])])

(defpartial home-posts
  [{:keys [id title stime]}]
  [:tr
   [:td id]
   [:td (elem/link-to (url-for "/post/:id" {:id id}) title)]
   [:td stime]])

(defpage "/" []
  (layout (login nil)))

(defpage "/home" []
  (layout
    [:div#posts
      [:table
        [:tbody
          (map home-posts (posts/get-latest-posts 20))]]]))

(defpage [:post "/login"] {:keys [email password]}
  (if (users/authenticate email password)
    (redirect "/home")
    (layout (login "Invalid email/password combination"))))

(defpage "/signup" []
  (layout (signup)))

(defpage [:post "/new_user"] user
  (users/create! user)
  (redirect (url-for "/home")))