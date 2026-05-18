(ns game.create.add-stage-actors.player-state-draw
  (:require [moon.ui.actor :as actor]
            [moon.draws :as draws]
            [moon.state :as state]))

(defn create [_ctx]
  {:type :ui/actor
   :draw! (fn [this _batch _parent-alpha]
            (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/stage this))
                  entity @player-eid
                  state-k (:state (:entity/fsm entity))]
              (draws/handle ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))})
