(ns jawaab.server
  (:use
    [noir.cookies :only [wrap-noir-cookies]]
    [noir.server :only [gen-handler load-views-ns]]))

(load-views-ns 'jawaab.views)

(def base-handler
  (gen-handler
    {:mode :dev
     :ns 'jawaab}))

(def app
  (-> base-handler
      wrap-noir-cookies))