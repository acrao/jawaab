(ns jawaab.views.users
  (:use
    [noir.core :only [defpartial defpage url-for]]
    [noir.response :only [redirect]]
    jawaab.views.common)
  (:require
   [jawaab.models.users :as users]))

(defpage [:post "/login"] {:keys [email password]}
  (let [uid (users/authenticate email password)]
    (if uid
      (do
        (users/save-uid! uid)
        (redirect "/home"))
      (layout (login "Invalid email/password combination")))))

(defpage "/logout" []
  (users/clear-cookies)
  (redirect (url-for "/")))

(defpage "/signup" []
  (layout (signup)))

(defpage [:post "/user/new"] user
  (let [uid (users/create user)]
    (users/save-uid! uid)
    (redirect (url-for "/home"))))