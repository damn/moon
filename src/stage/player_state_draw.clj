(ns stage.player-state-draw
  (:require [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [clojure.gdx.scene2d.actor.create :as actor]))

(defn create [_ctx]
  (actor/create
   {:draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (.getStage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draw! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
