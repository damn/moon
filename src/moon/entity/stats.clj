(ns moon.entity.stats
  (:require [malli.core :as m]
            [moon.color :as color]
            [moon.entity :as entity]
            [moon.ops :as ops]
            [moon.val-max :as val-max]))

(defn- get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops/apply (modifier-k modifiers)
             base-value))

(defn- ->pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

; TODO can just pass ops instead of modifiers modifier-k
(defn apply-max [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 1 get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [(min v mx) mx]]
  (assert (m/validate val-max/schema result) result)
  result))

; TODO can just pass ops instead of modifiers modifier-k
(defn apply-min [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 0 get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [v (max v mx)]]
    (assert (m/validate val-max/schema result) result)
    result))

(defn- add*    [mods other-mods] (merge-with ops/add    mods other-mods))
(defn- remove* [mods other-mods] (merge-with ops/remove mods other-mods))

; 1. name ! :entity/ -> :stats/
; 2. tests/protocols -> what are data structure of modifiers => is stat-k
; witha modifier key????
; how does the whole thing look like
; including editor based omgfwtf

(defn get-stat-value [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value base-value
               (:stats/modifiers stats)
               (keyword "modifier" (name stat-k)))))

(defn add    [stats mods] (update stats :stats/modifiers add*    mods))
(defn remove-mods [stats mods] (update stats :stats/modifiers remove* mods))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max mana modifiers :modifier/mana-max))

(defn not-enough-mana? [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max hp modifiers :modifier/hp-max))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))

(def ^:private hpbar-colors
  {:green     [0 0.8 0 1]
   :darkgreen [0 0.5 0 1]
   :yellow    [0.5 0.5 0 1]
   :red       [0.5 0 0 1]})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- draw-hpbar [world-unit-scale {:keys [body/position body/width body/height]} ratio]
  (let [[x y] position]
    (let [x (- x (/ width  2))
          y (+ y (/ height 2))
          height (* 5          world-unit-scale)
          border (* borders-px world-unit-scale)]
      [[:draw/filled-rectangle x y width height color/black]
       [:draw/filled-rectangle
        (+ x border)
        (+ y border)
        (- (* width ratio) (* 2 border))
        (- height          (* 2 border))
        (hpbar-color ratio)]])))

(defmethod entity/render :entity/stats
  [_ entity {:keys [ctx/world-unit-scale]}]
  (let [ratio (val-max/ratio (get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (draw-hpbar world-unit-scale
                  (:entity/body entity)
                  ratio))))
