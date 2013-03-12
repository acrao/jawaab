(ns jawaab.models.threads
  )

(defn get-latest-posts
  []
  ; Mocked
  (for [i (range 0 10)]
    {:id i :title "Mytitle" :submission-time i}))