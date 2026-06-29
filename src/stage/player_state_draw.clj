(ns stage.player-state-draw
  (:require [ctx.draw :refer [draw!]]
            [moon.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (Actor/.getStage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))}))
