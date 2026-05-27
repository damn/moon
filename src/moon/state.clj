(ns moon.state
  (:require [moon.stats :as stats]
            [moon.timer :as timer]))

(defmulti create
  (fn [[k _v] _eid _ctx]
    k))

(defmethod create :default
  [[_k v] _eid _ctx]
  v)

(defmulti enter
  (fn [[k _v] _eid]
    k))

(defmethod enter :default
  [[_k _v] _eid]
  nil)

(defmulti exit
  (fn [[k _v] _eid _ctx]
    k))

(defmethod exit :default
  [[_k _v] _eid _ctx]
  nil)

(defmulti cursor
  (fn [[k _v] _eid _ctx]
    k))

(defmulti pause-game?
  (fn [k]
    k))

(defmulti clicked-inventory-cell
  (fn [[k _v] _eid _cell]
    k))

(defmethod clicked-inventory-cell :default
  [_ _eid _cell]
  nil)

(defmulti draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))

(defmethod draw-ui-view :default
  [_ _eid _ctx]
  nil)

(defmulti handle-input
  (fn [[k _v] _eid _ctx]
    k))

(defmethod handle-input :default
  [_ _eid _ctx]
  nil)

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(defmethod create :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (timer/create elapsed-time))})

(defmethod create :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})

(defmethod create :player-moving
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})

(def reaction-time-multiplier 0.016)

(defmethod create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})
