(ns game.application
  (:require [game.ctx :as ctx]

            game.state.player-item-on-cursor

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

            game.effect.audiovisual
            game.effect.target-all
            game.effect.target-entity
            game.effect.spawn
            game.effect.projectile

            game.ui.data-viewer-window
            game.ui.error-window
            game.ui.property-editor-window
            game.ui.property-overview-window
            game.ui.dev-menu
            game.ui.action-bar
            game.ui.info-window

            [clojure.gdx :as gdx])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (gdx/put-colors! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (gdx/use-glfw-async!)
  (gdx/application!
   {:title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60

    :create!
    (fn [app]
      (gdx/set-tooltip-initial-time! 0)
      (reset! state (ctx/create! app)))

    :dispose!
    (fn []
      (ctx/dispose! @state))

    :render!
    (fn []
      (swap! state ctx/render!))

    :resize!
    (fn [width height]
      (ctx/resize! @state width height))

    :pause!
    (fn [])

    :resume!
    (fn [])}))
