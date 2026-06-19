(ns moon.val-max.ratio
  (:require [moon.val-max.validate :as validate]))

(defn f
  "If mx and v is 0, returns 0, otherwise (/ v mx)"
  [[^int v ^int mx]]
  {:pre [(validate/f [v mx])]}
  (if (and (zero? v) (zero? mx))
    0
    (/ v mx)))
