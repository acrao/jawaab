(ns jawaab.views.users
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [noir.response :only [redirect]]
    jawaab.views.common)
  (:require
   [jawaab.models.users :as users]))

(defpage [:post "/login"] {:keys [email password]}
  (if (users/authenticate email password)
    (redirect "/home")
    (layout (login "Invalid email/password combination"))))

(defpage "/signup" []
  (layout (signup)))

(defpage [:post "/user/new"] user
  (users/create! user)
  (redirect (url-for "/home")))