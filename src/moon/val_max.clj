(ns moon.val-max
  (:require [clojure.schema :as schema]
            [moon.malli :as malli-schema]
            [moon.mods :as mods]))

(defn- valid? [val-max]
  (malli-schema/validate schema/v val-max))

(defn to-pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

(defn ratio
  "If mx and v is 0, returns 0, otherwise (/ v mx)"
  [[^int v ^int mx]]
  {:pre [(valid? [v mx])]}
  (if (and (zero? v) (zero? mx))
    0
    (/ v mx)))

(defn apply-min [val-max modifiers modifier-k]
  (assert (valid? val-max) val-max)
  (let [val-max (update val-max 0 mods/get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [v (max v mx)]]
    (assert (valid? result) result)
    result))

(defn apply-max [val-max modifiers modifier-k]
  (assert (valid? val-max) val-max)
  (let [val-max (update val-max 1 mods/get-value modifiers modifier-k)
        [v mx] (to-pos-int val-max)
        result [(min v mx) mx]]
    (assert (valid? result) result)
    result))
