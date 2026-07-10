(ns clojure.moon.player-state-draw-create
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.moon.draw :refer [draw!]]
            [clojure.moon.entity-state-draw-ui-view :as entity-state-draw-ui-view]))

(defn player-state-draw-create [_ctx]
  (actor/new
   (fn [_actor _delta])
   (fn [this _batch _parent-alpha]
     (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/getStage this))
           entity @player-eid
           state-k (:state (:entity/fsm entity))]
       (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))))
