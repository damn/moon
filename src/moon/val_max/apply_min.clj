(ns moon.val-max.apply-min
  (:require [malli.validate :refer [validate]]
            [moon.modifiers :as modifiers]
            [moon.val-max :as val-max]
            [moon.val-max.to-pos-int :as ->pos-int]))

(defn f [val-max modifiers modifier-k]
  (assert (validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 0 modifiers/get-value modifiers modifier-k)
        [v mx] (->pos-int/f val-max)
        result [v (max v mx)]]
    (assert (validate val-max/schema result) result)
    result))
