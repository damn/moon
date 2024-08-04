(ns entity-state.npc-sleeping
  (:require [api.graphics :as g]
            [api.graphics.color :as color]
            [api.context :refer [world-grid ->counter]]
            [api.entity :as entity]
            [api.entity-state :as state]
            [api.world.cell :as cell]))

; TODO pass to creature data, also @ shout
(def ^:private aggro-range 6)

(defrecord NpcSleeping []
  state/State
  (enter [_ entity* _ctx])

  (exit [_ {:keys [entity/id
                   entity/position
                   entity/faction]} ctx]
    ; TODO make state = alerted, and shout at the end of that !
    ; then nice alert '!' and different entities different alert time
    [[:tx/add-text-effect id "[WHITE]!"]
     [:tx/create #:entity {:position position
                           :faction  faction
                           :shout (->counter ctx 0.2)}]])

  (tick [_ entity* context]
    (let [cell ((world-grid context) (entity/tile entity*))]
      (when-let [distance (cell/nearest-entity-distance @cell (entity/enemy-faction entity*))]
        (when (<= distance aggro-range)
          [[:tx/event (:entity/id entity*) :alert]]))))

  (render-below [_ entity* g ctx])
  (render-above [_ {[x y] :entity/position :keys [entity/body]} g _ctx]
    (g/draw-text g
                 {:text "zzz"
                  :x x
                  :y (+ y (:half-height body))
                  :up? true}))
  (render-info [_ entity* g ctx]))