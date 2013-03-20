(ns jawaab.views.tags
  (:use
    [noir.core :only [defpage]]
    jawaab.views.common)
  (:require
    [jawaab.models.tags :as tags]))

(defpage "/tags/:id/posts" {tag-id :id}
  (let [tag (tags/get-tag (Integer. tag-id))]
    (layout
      (posts-list (format "Posts tagged with %s" (:name tag))
        (tags/posts-by-tag (:id tag))))))