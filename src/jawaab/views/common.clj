(ns jawaab.views.common
  (:use
    [noir.core :only [defpartial url-for]]
    [noir.response :only [redirect]]
    [hiccup.page :only [include-css html5 include-js]])
  (:require
    [hiccup.element :as elem]
    [hiccup.form :as form]
    [jawaab.models.posts :as posts]
    [jawaab.models.users :as users]
    [jawaab.models.tags :as tags]))

(defpartial header
  []
   [:div#header.navbar.inverse.navbar-static-top
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
      (include-css "/css/bootstrap.min.css")
      (include-css "/css/styles.css")
      (include-css "/css/font-awesome.min.css")]
    (include-js "http://code.jquery.com/jquery-latest.js")
    (include-js "https://raw.github.com/coreyti/showdown/master/compressed/showdown.js")
    (include-js "/js/posts.js")
    [:body
      [:div.wrap
       (header)
        [:div.container
          [:div.row
            [:div.span12 content]]]]
      [:div#footer
        [:div.container
          [:p.muted.credit.text-center "Jawaab"]]]]))

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

(defpartial format-tags
  [tags]
  (doall (map
           #(elem/link-to {:class "badge badge-info tag-body"}
              (url-for "/tags/:id/posts" {:id (:tag_id %)}) (:name %))
           tags)))

(defpartial format-post-list-item
  [{:keys [id title stime body parent_id type]}]
  (when id
    [:div.row
      [:div.row
        [:div.span7
          [:strong
            (elem/link-to (url-for "/post/:id/view" {:id (if (= type "q") id parent_id)}  )
              (or title
                (str (subs body 0 (if (> (count body) 20) 20 (count body))) "...")))]]
        [:div.span2 [:p stime]]
        [:div.span1 [:p (posts/num-answers id)]]]
      [:div.row
        [:div.span10
          (format-tags (tags/tags-by-post id))]]
      [:hr]]))

(defpartial posts-list
  [header posts]
  (when header
    [:div.row.post-list-header
     [:h5 header]])
  [:div.row
    (elem/unordered-list {:class "home-list"}
      (map format-post-list-item posts))])