(ns stage.player-state-draw
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [ctx.draw :refer [draw!]]
            [moon.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [scene2d.actor :as actor]))

(defn create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (gdx-actor/get-stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))}))
