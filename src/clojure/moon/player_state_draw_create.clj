(ns clojure.moon.player-state-draw-create
  (:require [gdl.actor :as actor]
            [clojure.moon.draw :refer [draw!]]
            [clojure.moon.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [clojure.scene2d-actor :as scene2d-actor]))

(defn player-state-draw-create [_ctx]
  (scene2d-actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (actor/get-stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))}))
