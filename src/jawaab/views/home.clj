(ns jawaab.views.home
  (:use
    [noir.core :only [defpartial defpage render]]
    jawaab.views.common)
  (:require
    [clojure.tools.logging :as log]
    [hiccup.form :as form]
    [jawaab.models.threads :as threads]
    [jawaab.models.users :as users]
    ))

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
         (form/submit-button {:class "btn btn-primary"} "Submit")]])])

(defpartial home-posts
  [{:keys [id title submission-time]}]
  [:tr
   [:td id]
   [:td title]
   [:td submission-time]])

(defpage "/" []
  (layout
    (login nil)))

(defpage "/home" []
  (layout
    [:div#posts
      [:table
        [:tbody
          (map home-posts (threads/get-latest-posts))]]]))

(defpage [:post "/login"] {:keys [email password]}
  (if (users/authenticate email password)
    (render "/home")
    (layout (login "Invalid email/password combination"))))