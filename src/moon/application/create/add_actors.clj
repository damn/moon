(ns moon.game.create.add-actors
  (:require [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.game.create.ui.entity-info-window-config :as entity-info-window-config]
            [moon.game.create.ui.hp-mana-bar-config :as hp-mana-bar-config]
            [moon.game.create.ui.inventory-window :as inventory-window]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.scene2d.actor :as actor]
            [moon.ui :as ui]
            [moon.ui.info-window :as info-window]
            [moon.ui.message :as message]
            [moon.ui.stage :as stage]))

(defn- create-hp-mana-bar* [create-draws]
  {:type :actor/actor
   :act (fn [_this _delta])
   :draw (fn [actor _batch _parent-alpha]
           (when-let [stage (actor/stage actor)]
             (graphics/draw! (:ctx/graphics (stage/ctx stage))
                             (create-draws (stage/ctx stage)))))})

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

(def message-duration-seconds 0.5)

(declare step)

(defn rebuild-actors! [stage ctx]
  (stage/clear! stage)
  (step ctx))

(defn step [{:keys [ctx/stage] :as ctx}]
  (let [config (.config stage)]
    (doseq [actor [((:dev-menu config)
                    ctx
                    rebuild-actors!
                    (requiring-resolve 'moon.application.create.world/step)
                    (requiring-resolve 'moon.application.open-editor/do!))
                   {:type :actor/action-bar}
                   (create-hp-mana-bar* (hp-mana-bar-config/create ctx))
                   {:type :actor/group
                    :actor/name "moon.ui.windows"
                    :group/actors [(info-window/create (entity-info-window-config/create ctx))
                                   (inventory-window/create ctx)]}
                   {:type :actor/actor
                    :draw (fn [this _batch _parent-alpha]
                            (player-state-handle-draws (stage/ctx (actor/stage this))))
                    :act (fn [_ _delta])}
                   (message/create message-duration-seconds)]]
      (stage/add-actor! stage actor)))
  ctx)
