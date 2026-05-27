(ns game.application
  (:require [clojure.animation :as animation]
            [clojure.math.rectangle :as rectangle]
            [clojure.math.vector2 :as v]
            [moon.body :as body]
            [moon.grid :as grid]
            [moon.entity :as entity]
            [moon.timer :as timer]
            [game.ctx :as ctx]
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
            [clojure.gdx :as gdx])
  (:gen-class))

(defmethod entity/render :entity/clickable
  [[_k {:keys [text]}]
   {:keys [entity/body
           entity/mouseover?]}
   _ctx]
  (when (and mouseover? text)
    (let [[x y] (:body/position body)]
      [[:draw/text {:text text
                    :x x
                    :y (+ y (/ (:body/height body) 2))
                    :up? true}]])))

(defmethod entity/create :entity/animation
  [[_k {:keys [animation/frames
               animation/frame-duration
               animation/looping?
               delete-after-stopped?]}]
   _ctx]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})

(defmethod entity/tick :entity/animation
  [[_k animation] eid {:keys [ctx/delta-time]}]
  [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
   (when (and (:delete-after-stopped? animation)
              (animation/stopped? animation))
     [:tx/mark-destroyed eid])])

(defmethod entity/render :entity/animation
  [[_k animation] entity ctx]
  (entity/render [:entity/image (animation/current-frame animation)]
                 entity
                 ctx))

(defmethod entity/tick :entity/alert-friendlies-after-duration
  [[_k {:keys [counter faction]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/grid]}]
  (when (timer/stopped? elapsed-time counter)
    (cons [:tx/mark-destroyed eid]
          (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                   :radius 4}
                                  (grid/circle->entities grid)
                                  (filter #(= (:entity/faction @%) faction)))]
            [:tx/event friendly-eid :alert]))))

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
