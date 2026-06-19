(ns moon.val-max.apply-max
  (:require [moon.modifiers :as modifiers]
            [moon.val-max.validate :as validate]
            [moon.val-max.to-pos-int :as ->pos-int]))

(defn f [val-max modifiers modifier-k]
  (assert (validate/f val-max) val-max)
  (let [val-max (update val-max 1 modifiers/get-value modifiers modifier-k)
        [v mx] (->pos-int/f val-max)
        result [(min v mx) mx]]
    (assert (validate/f result) result)
    result))
