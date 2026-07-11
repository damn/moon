(ns moon.val-max
  (:require [moon.malli :as malli-schema]
            [moon.mods :as mods]))

(def schema
  (malli-schema/create
   [:and
    [:vector {:min 2 :max 2} [:int {:min 0}]]
    [:fn {:error/fn (fn [{[^int v ^int mx] :value} _]
                      (when (< mx v)
                        (format "Expected max (%d) to be smaller than val (%d)" v mx)))}
     (fn [[^int a ^int b]] (<= a b))]]))

(defn- valid? [val-max]
  (malli-schema/validate schema val-max))

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
