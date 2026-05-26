(ns game.application
  (:require game.entity.animation
            game.entity.alert-friendlies-after-duration
            game.entity.body
            game.entity.clickable
            game.entity.delete-after-duration
            game.entity.fsm
            game.entity.image
            game.entity.inventory
            game.entity.line-render
            game.entity.mouseover
            game.entity.movement
            game.entity.skills
            game.entity.stats
            game.entity.string-effect
            game.entity.temp-modifier
            game.entity.projectile-collision
            game.state.active-skill
            game.state.npc-idle
            game.state.npc-sleeping
            game.state.npc-moving
            game.state.player-item-on-cursor
            game.state.stunned
            game.schema-widget.animation
            game.schema-widget.boolean
            game.schema-widget.default
            game.schema-widget.enum
            game.schema-widget.image
            game.schema-widget.map
            game.schema-widget.number
            game.schema-widget.one-to-many
            game.schema-widget.one-to-one
            game.schema-widget.sound
            game.schema-widget.string
            game.schema-widget.val-max
            game.state-impl
            game.effect.audiovisual
            game.effect.target-all
            game.effect.target-entity
            game.effect.spawn
            game.effect.projectile
            game.effect-impl
            game.info-impl
            game.ui.data-viewer-window
            game.ui.error-window
            game.ui.property-editor-window
            game.ui.property-overview-window
            game.ui.dev-menu
            game.ui.action-bar
            game.ui.info-window
            [clojure.config :refer [edn-resource]]
            [clojure.gdx :as gdx])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [create
                dispose!
                render
                resize!]
         :as config} (edn-resource "start.edn")]
    (gdx/application!
     (merge config
            {:create!
             (fn [app]
               (reset! state
                       (reduce (fn [ctx [f & params]]
                                 (apply f ctx params))
                               app
                               create)))

             :dispose!
             (fn []
               (dispose! @state))

             :render!
             (fn []
               (swap! state
                      (fn [ctx]
                        (reduce (fn [ctx [f & params]]
                                  (apply f ctx params))
                                ctx
                                render))))

             :resize!
             (fn [width height]
               (resize! @state width height))

             :pause!
             (fn [])

             :resume!
             (fn [])}))))
