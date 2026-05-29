(ns stage.player-state-draw
  (:require [game.ctx :as ctx]
            [game.state :as state]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create [_ctx]
  (actor/create
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (ctx/draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
