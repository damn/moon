(ns clojure.val-max.apply-max
  (:require [clojure.modifiers-get-value :as get-value]
            [clojure.val-max.validate :as validate]
            [clojure.to-pos-int :as ->pos-int]))

(defn f [val-max modifiers modifier-k]
  (assert (validate/f val-max) val-max)
  (let [val-max (update val-max 1 get-value/f modifiers modifier-k)
        [v mx] (->pos-int/f val-max)
        result [(min v mx) mx]]
    (assert (validate/f result) result)
    result))
