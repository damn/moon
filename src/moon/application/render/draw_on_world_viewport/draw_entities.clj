(ns moon.application.render.draw-on-world-viewport.draw-entities
  (:require [moon.color :as color]
            [moon.effects.target-all :as target-all]
            [moon.effects.target-entity :as target-entity]
            [moon.entity.animation :as animation]
            [moon.entity.faction :as faction]
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.entity.stats :as stats]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.throwable :as throwable]
            [moon.timer :as timer]
            [moon.ui :as ui]
            [moon.utils :as utils]
            [moon.val-max :as val-max]
            [moon.world.raycaster :as raycaster]))

(defn- draw-image
  [image
   {:keys [entity/body]}
   {:keys [ctx/graphics]}]
  [[:draw/texture-region
    (graphics/texture-region graphics image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(def ^:private hpbar-colors
  {:green     [0 0.8 0 1]
   :darkgreen [0 0.5 0 1]
   :yellow    [0.5 0.5 0 1]
   :red       [0.5 0 0 1]})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- draw-hpbar [world-unit-scale {:keys [body/position body/width body/height]} ratio]
  (let [[x y] position]
    (let [x (- x (/ width  2))
          y (+ y (/ height 2))
          height (* 5          world-unit-scale)
          border (* borders-px world-unit-scale)]
      [[:draw/filled-rectangle x y width height color/black]
       [:draw/filled-rectangle
        (+ x border)
        (+ y border)
        (- (* width ratio) (* 2 border))
        (- height          (* 2 border))
        (hpbar-color ratio)]])))

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-skill-image
  [texture-region entity [x y] action-counter-ratio]
  (let [radius skill-image-radius-world-units
        y (+ (float y)
             (float (/ (:body/height (:entity/body entity)) 2))
             (float 0.15))
        center [x (+ y radius)]]
    [[:draw/filled-circle center radius [1 1 1 0.125]]
     [:draw/sector
      center
      radius
      90 ; start-angle
      (* (float action-counter-ratio) 360) ; degree
      [1 1 1 0.5]]
     [:draw/texture-region texture-region [(- (float x) radius) y]]]))

(def effect-k->fn
  {:effects/target-all {:draw (fn [_
                                   {:keys [effect/source]}
                                   {:keys [ctx/world]}]
                                (let [{:keys [world/active-entities]} world
                                      source* @source]
                                  (for [target* (map deref (target-all/affected-targets active-entities world source*))]
                                    [:draw/line
                                     (:body/position (:entity/body source*)) #_(start-point source* target*)
                                     (:body/position (:entity/body target*))
                                     [1 0 0 0.5]])))}

   :effects/target-entity {:draw (fn [[_ {:keys [maxrange]}]
                                      {:keys [effect/source effect/target]}
                                      _ctx]
                                   (when target
                                     (let [body        (:entity/body @source)
                                           target-body (:entity/body @target)]
                                       [[:draw/line
                                         (target-entity/start-point body target-body)
                                         (target-entity/end-point body target-body maxrange)
                                         (if (target-entity/in-range? body target-body maxrange)
                                           [1 0 0 0.5]
                                           [1 1 0 0.5])]])))}})

(defn draw-effect [{k 0 :as component} effect-ctx ctx]
  (if-let [f (:draw (effect-k->fn k))]
    (f component effect-ctx ctx)
    nil))

(def ^:private render-layers
  (let [outline-alpha 0.4
        enemy-color [1 0 0 outline-alpha]
        friendly-color [0 1 0 outline-alpha]
        neutral-color [1 1 1 outline-alpha]
        mouseover-ellipse-width 5]
    [{:entity/mouseover?     (fn
                               [_
                                {:keys [entity/body
                                        entity/faction]}
                                {:keys [ctx/world]}]
                               (let [player @(:world/player-eid world)]
                                 [[:draw/with-line-width mouseover-ellipse-width
                                   [[:draw/ellipse
                                     (:body/position body)
                                     (/ (:body/width  body) 2)
                                     (/ (:body/height body) 2)
                                     (cond (= faction (faction/enemy (:entity/faction player)))
                                           enemy-color
                                           (= faction (:entity/faction player))
                                           friendly-color
                                           :else
                                           neutral-color)]]]]))

      :stunned               (fn [_ {:keys [entity/body]} _ctx]
                               [[:draw/circle
                                 (:body/position body)
                                 0.5
                                 [1 1 1 0.6]]])

      :player-item-on-cursor (fn
                               [{:keys [item]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/input
                                        ctx/stage]}]
                               ; TODO do not draw here, only at UI view
                               ; then graphics can draw world without stage/input
                               (when (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input)))
                                 [[:draw/texture-region
                                   (graphics/texture-region graphics (:entity/image item))
                                   (player-item-on-cursor/item-place-position (:graphics/world-mouse-position graphics)
                                                                              entity)
                                   {:center? true}]]))}
     {:entity/clickable      (fn
                               [{:keys [text]}
                                {:keys [entity/body
                                        entity/mouseover?]}
                                _ctx]
                               (when (and mouseover? text)
                                 (let [[x y] (:body/position body)]
                                   [[:draw/text {:text text
                                                 :x x
                                                 :y (+ y (/ (:body/height body) 2))
                                                 :up? true}]])))

      :entity/animation      (fn [animation entity ctx]
                               (draw-image (animation/current-frame animation)
                                           entity
                                           ctx))

      :entity/image          draw-image

      :entity/line-render    (fn [{:keys [thick? end color]} {:keys [entity/body]} _ctx]
                               (let [position (:body/position body)]
                                 (if thick?
                                   [[:draw/with-line-width
                                     4
                                     [[:draw/line position end color]]]]
                                   [[:draw/line position end color]])))}

     {:npc-sleeping          (fn [_ {:keys [entity/body]} _ctx]
                               (let [[x y] (:body/position body)]
                                 [[:draw/text {:text "zzz"
                                               :x x
                                               :y (+ y (/ (:body/height body) 2))
                                               :up? true}]]))

      :entity/temp-modifier  (fn [_ entity _ctx]
                               [[:draw/filled-circle
                                 (:body/position (:entity/body entity))
                                 0.5
                                 [0.5 0.5 0.5 0.4]]])

      :entity/string-effect  (fn [{:keys [text]} entity {:keys [ctx/graphics]}]
                               (let [[x y] (:body/position (:entity/body entity))]
                                 [[:draw/text {:text text
                                               :x x
                                               :y (+ y
                                                     (/ (:body/height (:entity/body entity)) 2)
                                                     (* 5 (:graphics/world-unit-scale graphics)))
                                               :scale 2
                                               :up? true}]]))}

     {:entity/stats          (fn [_ entity {:keys [ctx/graphics]}]
                               (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
                                 (when (or (< ratio 1) (:entity/mouseover? entity))
                                   (draw-hpbar (:graphics/world-unit-scale graphics)
                                               (:entity/body entity)
                                               ratio))))
      :active-skill          (fn
                               [{:keys [skill effect-ctx counter]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/world]
                                 :as ctx}]
                               (let [{:keys [entity/image skill/effects]} skill]
                                 (concat (draw-skill-image (graphics/texture-region graphics image)
                                                           entity
                                                           (:body/position (:entity/body entity))
                                                           (timer/ratio (:world/elapsed-time world) counter))
                                         (mapcat #(draw-effect % effect-ctx ctx)  ; update-effect-ctx here too ?
                                                 effects))))}]))

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color]]))

(defn- draw-entity
  [{:keys [ctx/graphics]
    :as ctx}
   entity render-layer]
  (try (do
        (when show-body-bounds?
          (graphics/draw! graphics (draw-body-rect (:entity/body entity)
                                                   (if (:body/collides? (:entity/body entity))
                                                     color/white
                                                     color/gray))))
        (doseq [[k v] entity
                :let [draw-fn (get render-layer k)]
                :when draw-fn]
          (graphics/draw! graphics (draw-fn v entity ctx))))
       (catch Throwable t
         (graphics/draw! graphics (draw-body-rect (:entity/body entity) color/red))
         (throwable/pretty-pst t))))

(defn do!
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [entities (map deref (:world/active-entities world))
        player @(:world/player-eid world)
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? world player entity)))]
    (doseq [[z-order entities] (utils/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    (:world/render-z-order world))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))
