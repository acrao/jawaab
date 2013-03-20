(ns jawaab.views.home
  (:use
    [noir.core :only [defpartial defpage url-for]]
    jawaab.views.common)
  (:require
    [jawaab.models.users :as users]
    [hiccup.element :as elem]
    [jawaab.models.posts :as posts]
    [jawaab.models.tags :as tags]))

(defpage "/" []
  (layout (login nil)))

(defpage "/home" []
  (layout
    (posts-list nil (posts/get-latest-posts 20))))

(defpage "/search" {:keys [query-string]}
  (layout
    (posts-list (format "Search results for \"%s\"" query-string)
      (posts/search query-string))))