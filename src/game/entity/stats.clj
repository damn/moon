(ns game.entity.stats
  (:require [game.ctx :as ctx]
            [malli.core :as m]
            [moon.entity :as entity]
            [moon.stats :as stats]
            [moon.ops :as ops]
            [moon.val-max :as val-max]))

(defn- get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops/apply (modifier-k modifiers)
             base-value))

(defn- ->pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

; TODO can just pass ops instead of modifiers modifier-k
(defn- apply-max [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 1 get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [(min v mx) mx]]
  (assert (m/validate val-max/schema result) result)
  result))

; TODO can just pass ops instead of modifiers modifier-k
(defn- apply-min [val-max modifiers modifier-k]
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

(extend-type clojure.lang.IPersistentMap
  stats/Stats
  (get-stat-value [stats stat-k]
    (when-let [base-value (stat-k stats)]
      (get-value base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))

  (add [stats mods]
    (update stats :stats/modifiers add* mods))

  (remove-mods [stats mods]
    (update stats :stats/modifiers remove* mods))

  (get-mana [{:keys [stats/mana
                     stats/modifiers]}]
    (apply-max mana modifiers :modifier/mana-max))

  (not-enough-mana? [stats {:keys [skill/cost]}]
    (> cost ((stats/get-mana stats) 0)))

  (pay-mana-cost [stats cost]
    (let [mana-val ((stats/get-mana stats) 0)]
      (assert (<= cost mana-val))
      (assoc-in stats [:stats/mana 0] (- mana-val cost))))

  (get-hitpoints [{:keys [stats/hp
                          stats/modifiers]}]
    (apply-max hp modifiers :modifier/hp-max))

  (calc-damage
    ([source target damage]
     (update (stats/calc-damage source damage)
             :damage/min-max
             apply-max
             (:stats/modifiers target)
             :modifier/damage-receive-max))
    ([source damage]
     (update damage
             :damage/min-max
             #(-> %
                  (apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                  (apply-max (:stats/modifiers source) :modifier/damage-deal-max))))))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))

(defmethod entity/render :entity/stats
  [_ entity {:keys [ctx/colors] :as ctx}]
  (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 (ctx/world-unit-scale ctx))
            border (* 1 (ctx/world-unit-scale ctx))]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))
