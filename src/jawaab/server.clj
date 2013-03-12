(ns jawaab.server
  (:require
    [noir.server :as server]))

(server/load-views-ns 'jawaab.views)

(def handler
  (server/gen-handler
    {:mode :dev
     :ns 'jawaab}))