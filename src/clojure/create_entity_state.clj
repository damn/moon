(ns clojure.create-entity-state
  (:require [clojure.apply-action-speed-modifier :as apply-action-speed-modifier]
            [clojure.get-stat-value :refer [get-stat-value]]
            [clojure.timer-create :refer [create-timer]]))

(defmulti f
  (fn [[k _v] _eid _ctx]
    k))

(defmethod f :default
  [[_k v] _eid _ctx]
  v)

(defmethod f :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier/f @eid skill)
                 (create-timer elapsed-time))})

(defmethod f :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (create-timer elapsed-time duration)})

(defmethod f :player-moving
  [[_k movement-vector] _eid _ctx]
  {:movement-vector movement-vector})

(defmethod f :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (create-timer elapsed-time
                        (* (get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})

(defmethod f :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})
