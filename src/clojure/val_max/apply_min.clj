(ns clojure.val-max.apply-min
  (:require [clojure.modifiers-get-value :as get-value]
            [clojure.val-max.validate :as validate]
            [clojure.to-pos-int :as ->pos-int]))

(defn f [val-max modifiers modifier-k]
  (assert (validate/f val-max) val-max)
  (let [val-max (update val-max 0 get-value/f modifiers modifier-k)
        [v mx] (->pos-int/f val-max)
        result [v (max v mx)]]
    (assert (validate/f result) result)
    result))
