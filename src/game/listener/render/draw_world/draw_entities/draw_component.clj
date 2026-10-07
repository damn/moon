(ns game.listener.render.draw-world.draw-entities.draw-component
  (:require [clojure.math :as math]
            [game.draw :refer [draw-fn-filled-rectangle
                               draw-fn-text
                               draw-fn-texture-region]]
            [game.spawn :refer [item-place-position]]
            [game.target-all :refer [affected-targets]]
            [moon.body :as body]
            [moon.faction :as faction]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.timer :as timer]
            [moon.val-max :as val-max])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(def ^:private active-skill-radius
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-fn-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.circle ^ShapeDrawer shape-drawer x y radius))

(defn- draw-fn-ellipse [shape-drawer [x y] radius-x radius-y color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.ellipse ^ShapeDrawer shape-drawer x y radius-x radius-y))

(defn- draw-fn-filled-circle [shape-drawer [x y] radius color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledCircle ^ShapeDrawer shape-drawer (float x) (float y) (float radius)))

(defn- draw-fn-line [shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.line ^ShapeDrawer shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn- draw-fn-sector [shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.sector ^ShapeDrawer shape-drawer center-x center-y radius start-radians radians))

(defn- draw-with-line-width! [shape-drawer width draw-body]
  (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* width old-line-width))
    (draw-body)
    (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width)))

(defn- effect-render
  [[k v] effect-ctx shape-drawer active-entities colors raycaster]
  (case k
    :effects/target-all
    (let [source (:effect/source effect-ctx)
          source* @source]
      (doseq [target* (map deref (affected-targets active-entities raycaster source*))]
        (draw-fn-line shape-drawer
                      (:entity/position source*)
                      (:entity/position target*)
                      (:colors/target-all-render colors))))

    :effects/target-entity
    (when-let [target (:effect/target effect-ctx)]
      (let [source (:effect/source effect-ctx)
            body        @source
            target-body @target
            maxrange (:maxrange v)]
        (draw-fn-line shape-drawer
                      (body/start-point body target-body)
                      (body/end-point body target-body maxrange)
                      (if (body/in-range? body target-body maxrange)
                        (:colors/target-entity-in-range colors)
                        (:colors/target-entity-not-in-range colors)))))

    nil))

(defn draw-component
  [shape-drawer batch default-font unit-scale world-unit-scale mouseover-actor world-mouse-position
   textures colors player elapsed-time active-entities raycaster
   entity k v]
  (case k
    :entity/clickable
    (let [{:keys [text]} v
          {:keys [entity/position entity/height entity/mouseover?]} entity]
      (when (and mouseover? text)
        (let [[x y] position]
          (draw-fn-text batch default-font unit-scale {:text text
                             :x x
                             :y (+ y (/ height 2))
                             :up? true}))))

    :player-item-on-cursor
    (let [{:keys [item]} v]
      (when-not mouseover-actor
        (draw-fn-texture-region batch unit-scale
                                (textures/texture-region textures (:entity/image item))
                                (item-place-position (:entity/position entity)
                                                     world-mouse-position
                                                     (- (:entity/click-distance-tiles entity) 0.1))
                                {:center? true})))

    :entity/animation
    (let [{:keys [frames cnt frame-duration]} v
          image (frames (min (int (/ (float cnt) (float frame-duration)))
                             (dec (count frames))))]
      (draw-fn-texture-region batch unit-scale
                              (textures/texture-region textures image)
                              (:entity/position entity)
                              {:center? true
                               :rotation (or (:entity/rotation-angle entity) 0)}))

    :entity/image
    (draw-fn-texture-region batch unit-scale
                            (textures/texture-region textures v)
                            (:entity/position entity)
                            {:center? true
                             :rotation (or (:entity/rotation-angle entity) 0)})

    :entity/line-render
    (let [{:keys [thick? end color]} v
          position (:entity/position entity)]
      (if thick?
        (draw-with-line-width! shape-drawer 4 #(draw-fn-line shape-drawer position end color))
        (draw-fn-line shape-drawer position end color)))

    :entity/mouseover?
    (let [{:keys [entity/position entity/width entity/height entity/faction]} entity
          color (cond (= faction (faction/enemy (:entity/faction player)))
                      (:colors/enemy-color colors)
                      (= faction (:entity/faction player))
                      (:colors/friendly-color colors)
                      :else
                      (:colors/neutral-color colors))]
      (draw-with-line-width! shape-drawer 5
                             #(draw-fn-ellipse shape-drawer position
                                                (/ width 2)
                                                (/ height 2)
                                                color)))

    :entity/stats
    (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
      (when (or (< ratio 1) (:entity/mouseover? entity))
        (let [{:keys [entity/position entity/width entity/height]} entity
              [x y] position
              x (- x (/ width  2))
              y (+ y (/ height 2))
              height (* 5 world-unit-scale)
              border (* 1 world-unit-scale)]
          (draw-fn-filled-rectangle shape-drawer x y width height (:colors/hp-bar-rect colors))
          (draw-fn-filled-rectangle shape-drawer
                                    (+ x border)
                                    (+ y border)
                                    (- (* width ratio) (* 2 border))
                                    (- height (* 2 border))
                                    ((:colors/hp-bar colors) ratio)))))

    :entity/string-effect
    (let [{:keys [text]} v
          [x y] (:entity/position entity)]
      (draw-fn-text batch default-font unit-scale {:text text
                         :x x
                         :y (+ y
                               (/ (:entity/height entity) 2)
                               (* 5 world-unit-scale))
                         :scale 2
                         :up? true}))

    :entity/temp-modifier
    (draw-fn-filled-circle shape-drawer (:entity/position entity) 0.5 (:colors/temp-modifier colors))

    :active-skill
    (let [{:keys [skill effect-ctx counter]} v
          {:keys [entity/image skill/effects]} skill
          radius active-skill-radius
          action-counter-ratio (timer/ratio elapsed-time counter)
          texture-region (textures/texture-region textures image)
          [x y] (:entity/position entity)
          y (+ (float y)
               (float (/ (:entity/height entity) 2))
               (float 0.15))
          center [x (+ y radius)]]
      (draw-fn-filled-circle shape-drawer center radius (:colors/active-skill-circle colors))
      (draw-fn-sector shape-drawer
                      center
                      radius
                      (math/to-radians 90)
                      (math/to-radians (* (float action-counter-ratio) 360))
                      (:colors/active-skill-sector colors))
      (draw-fn-texture-region batch unit-scale texture-region [(- (float x) radius) y])
      (doseq [effect effects]
        (effect-render effect effect-ctx shape-drawer
                       active-entities
                       colors
                       raycaster)))

    :npc-sleeping
    (let [{:keys [entity/position entity/height]} entity
          [x y] position]
      (draw-fn-text batch default-font unit-scale {:text "zzz"
                         :x x
                         :y (+ y (/ height 2))
                         :up? true}))

    :stunned
    (draw-fn-circle shape-drawer (:entity/position entity) 0.5 (:colors/stunned colors))))
