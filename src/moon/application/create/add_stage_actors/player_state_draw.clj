(ns moon.application.create.add-stage-actors.player-state-draw
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.input :as input]
            [moon.draws :as draws]
            [moon.state :as state]
            [moon.textures :as textures]))

(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/input
                 ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (input/mouse-position input))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))

(defn create []
  (actor/create
   {:type :ui/actor
    :draw! (fn [this _batch _parent-alpha]
             (let [{:keys [ctx/player-eid] :as ctx} (stage/ctx (actor/stage this))
                   entity @player-eid
                   state-k (:state (:entity/fsm entity))]
               (draws/handle ctx (state/draw-ui-view [state-k (state-k entity)] player-eid ctx))))}))
