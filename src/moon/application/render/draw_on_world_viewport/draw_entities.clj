(ns moon.application.render.draw-on-world-viewport.draw-entities
  (:require moon.player-item-on-cursor
            [clojure.animation :as animation]
            [moon.stage :as stage]
            [moon.app :as app]
            [clojure.math :as math]
            [moon.draws :as draws]
            [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.faction :as faction]
            [moon.throwable :as throwable]
            [moon.raycaster :as raycaster]
            [moon.timer :as timer]
            [moon.textures :as textures]
            [moon.stats :as stats]
            [moon.order :as order]
            [moon.val-max :as val-max]))

(defmethod entity/render :entity/temp-modifier
  [_ entity {:keys [ctx/colors]}]
  [[:draw/filled-circle
    (:body/position (:entity/body entity))
    0.5
    (:colors/temp-modifier colors)]])

(defmethod entity/render :npc-sleeping
  [_ {:keys [entity/body]} _ctx]
  (let [[x y] (:body/position body)]
    [[:draw/text {:text "zzz"
                  :x x
                  :y (+ y (/ (:body/height body) 2))
                  :up? true}]]))

(defmethod entity/render :player-item-on-cursor
  [[_k {:keys [item]}]
   entity
   {:keys [ctx/app
           ctx/stage
           ctx/textures
           ctx/world-mouse-position]}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (app/mouse-position app))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (moon.player-item-on-cursor/item-place-position world-mouse-position entity)
      {:center? true}]]))

(defmethod entity/render :stunned
  [_ {:keys [entity/body]} {:keys [ctx/colors]}]
  [[:draw/circle
    (:body/position body)
    0.5
    (:colors/stunned colors)]])

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defmethod entity/render :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   entity
   {:keys [ctx/colors
           ctx/elapsed-time
           ctx/textures]
    :as ctx}]
  (let [{:keys [entity/image skill/effects]} skill]
    (concat (let [action-counter-ratio (timer/ratio elapsed-time counter)
                  texture-region (textures/texture-region textures image)
                  radius skill-image-radius-world-units
                  [x y] (:body/position (:entity/body entity))
                  y (+ (float y)
                       (float (/ (:body/height (:entity/body entity)) 2))
                       (float 0.15))
                  center [x (+ y radius)]]
              [[:draw/filled-circle center radius (:colors/active-skill-circle colors)]
               [:draw/sector
                center
                radius
                (math/to-radians 90) ; start-angle
                (math/to-radians (* (float action-counter-ratio) 360)) ; degree
                (:colors/active-skill-sector colors)]
               [:draw/texture-region texture-region [(- (float x) radius) y]]])
            (mapcat #(effect/render % effect-ctx ctx)  ; update-effect-ctx here too ?
                    effects))))

(def mouseover-ellipse-width 5)

(defmethod entity/render :entity/animation
  [[_k animation] entity ctx]
  (entity/render [:entity/image (animation/current-frame animation)]
                 entity
                 ctx))

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

(defmethod entity/render :entity/image
  [[_k image] {:keys [entity/body]} {:keys [ctx/textures]}]
  [[:draw/texture-region
    (textures/texture-region textures image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(defmethod entity/render :entity/line-render
  [[_k {:keys [thick? end color]}]
   {:keys [entity/body]}
   _ctx]
  (let [position (:body/position body)]
    (if thick?
      [[:draw/with-line-width
        4
        [[:draw/line position end color]]]]
      [[:draw/line position end color]])))

(defmethod entity/render :entity/mouseover?
  [_
   {:keys [entity/body
           entity/faction]}
   {:keys [ctx/colors
           ctx/player-eid]}]
  (let [player @player-eid]
    [[:draw/with-line-width mouseover-ellipse-width
      [[:draw/ellipse
        (:body/position body)
        (/ (:body/width  body) 2)
        (/ (:body/height body) 2)
        (cond (= faction (faction/enemy (:entity/faction player)))
              (:colors/enemy-color colors)
              (= faction (:entity/faction player))
              (:colors/friendly-color colors)
              :else
              (:colors/neutral-color colors))]]]]))

(defmethod entity/render :entity/stats
  [_ entity {:keys [ctx/colors
                    ctx/world-unit-scale]}]
  (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
    (when (or (< ratio 1) (:entity/mouseover? entity))
      (let [{:keys [body/position body/width body/height]} (:entity/body entity)
            [x y] position
            x (- x (/ width  2))
            y (+ y (/ height 2))
            height (* 5 world-unit-scale)
            border (* 1 world-unit-scale)]
        [[:draw/filled-rectangle x y width height (:colors/hp-bar-rect colors)]
         [:draw/filled-rectangle
          (+ x border)
          (+ y border)
          (- (* width ratio) (* 2 border))
          (- height          (* 2 border))
          ((:colors/hp-bar colors) ratio)]]))))

(defmethod entity/render :entity/string-effect
  [[_k {:keys [text]}] entity {:keys [ctx/world-unit-scale]}]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 world-unit-scale))
                  :scale 2
                  :up? true}]]))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))

(defn- draw-entity
  [{:keys [ctx/colors] :as ctx} entity render-layer]
  (try (do
        (when show-body-bounds?
          (draws/handle ctx (draw-body-rect (:entity/body entity)
                                             (if (:body/collides? (:entity/body entity))
                                               (:colors/debug-body-outline-collides colors)
                                               (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (draws/handle ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (draws/handle ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
         (throwable/pretty-pst t))))

(defn do!
  [{:keys [ctx/active-entities
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (order/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))
