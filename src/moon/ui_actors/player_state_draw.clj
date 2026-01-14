(ns moon.ui-actors.player-state-draw
  (:require [moon.entity.state :as state]
            [moon.graphics :as graphics])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [_ctx]
  (proxy [Actor] []
    (draw [_batch _parent-alpha]
      (let [{:keys [ctx/graphics
                    ctx/world]
             :as ctx} (.ctx (.getStage this))
            player-eid (:world/player-eid world)
            entity @player-eid
            state-k (:state (:entity/fsm entity))]
        (graphics/draw! graphics (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))))
