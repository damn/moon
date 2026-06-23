(ns stage.player-state-draw
  (:require [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [gdl.get-stage :refer [get-stage]]
            [scene2d.actor :as actor]))

(defn create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (get-stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
