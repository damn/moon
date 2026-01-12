(ns moon.ui-actors.player-state-draw
  (:require [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.ui :as ui]
            [moon.ui.actor :as actor]
            [moon.ui.stage :as stage]))

; TODO to 'entity.state'  protocol or 'state' protocol?
(def state->draw-ui-view
  {:player-item-on-cursor (fn
                            [eid
                             {:keys [ctx/graphics
                                     ctx/input
                                     ctx/stage]}]
                            ; TODO see player-item-on-cursor at render layers
                            ; always draw it here at right position, then render layers does not need input/stage
                            ; can pass world to graphics, not handle here at application
                            (when (not (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input))))
                              [[:draw/texture-region
                                (graphics/texture-region graphics (:entity/image (:entity/item-on-cursor @eid)))
                                (:graphics/ui-mouse-position graphics)
                                {:center? true}]]))})

(defn- player-state-handle-draws
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [player-eid (:world/player-eid world)
        entity @player-eid
        state-k (:state (:entity/fsm entity))]
    (when-let [f (state->draw-ui-view state-k)]
      (graphics/draw! graphics (f player-eid ctx)))))

(defn create [_ctx]
  (actor/create
   {:draw (fn [this _batch _parent-alpha]
            (player-state-handle-draws (stage/ctx (.getStage this))))
    :act (fn [_ _delta])}))
