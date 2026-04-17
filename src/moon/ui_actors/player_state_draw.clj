(ns moon.ui-actors.player-state-draw
  (:require [clojure.scene2d.actor :as actor]
            [moon.draws :as draws]
            [clojure.scene2d.stage :as stage]
            [moon.state :as state]
            [moon.ui :as ui]))

(defn create [_ctx]
  (ui/create
   {:type :ui/actor
    :draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (stage/ctx (actor/stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draws/handle! ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
