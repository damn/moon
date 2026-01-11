(ns moon.faction)

(defn enemy [faction]
  (case faction
    :evil :good
    :good :evil))
