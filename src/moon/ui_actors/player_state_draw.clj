(ns moon.ui-actors.player-state-draw
  (:require [moon.ctx :as ctx]
            [moon.state :as state])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (moon Stage)))

(defn create [_ctx]
  (proxy [Actor] []
    (draw [_batch _parent-alpha]
      (let [{:keys [ctx/player-eid] :as ctx} (.ctx ^Stage (Actor/.getStage this))
            entity @player-eid
            state-k (:state (:entity/fsm entity))]
        (ctx/draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))))
