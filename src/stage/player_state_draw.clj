(ns stage.player-state-draw
  (:require [ctx.draw :refer [draw!]]
            [game.state :as state]
            [scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (Actor/.getStage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
