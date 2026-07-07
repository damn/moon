(ns clojure.player-state-draw
  (:require
            [clojure.get-stage]
            [clojure.draw :refer [draw!]]
            [clojure.entity-state-draw-ui-view :as entity-state-draw-ui-view]
            [clojure.scene2d-actor :as actor]))

(defn create [_ctx]
  (actor/f
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (clojure.get-stage/f this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (entity-state-draw-ui-view/f [state-k (state-k entity)] player-eid ctx))))}))
