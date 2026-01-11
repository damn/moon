(ns moon.ui-actors.player-state-draw
  "The function which receives the context object and creates the dev menu user interface actor.

  Game application domain function object - we dont have 'types' but 'functions'.
  The game is made of special functions serving special use.
  This one is for create?"
  (:require
    [moon.animation]
    [moon.body]
    [moon.ctx :as ctx]

    [moon.entity.skills]

    [moon.entity.state-impl]
    [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]

    [moon.graphics :as graphics]                            ; 'creature' ?
    [moon.input :as input]

    [moon.ui :as ui]

    [moon.ui.actor :as actor]

    [moon.ui.editor.widgets-impl]
    [moon.ui.editor.window]

    [moon.ui.stage :as stage]

    [moon.world-fns.creature-tiles]
    )
)

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
            (player-state-handle-draws (stage/ctx (actor/stage this))))
    :act (fn [_ _delta])}))
