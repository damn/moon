(ns moon.entity.faction)

(defn enemy [faction]
  (case faction
    :evil :good
    :good :evil))
