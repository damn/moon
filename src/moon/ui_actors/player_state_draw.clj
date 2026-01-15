(ns moon.ui-actors.player-state-draw
  (:require [moon.entity.state :as state]
            [moon.graphics :as graphics])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [_ctx]
  (proxy [Actor] []
    (draw [_batch _parent-alpha]
      (let [{:keys [ctx/graphics
                    ctx/player-eid]
             :as ctx} (.ctx (.getStage this))
            entity @player-eid
            state-k (:state (:entity/fsm entity))]
        (graphics/draw! graphics (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))))
