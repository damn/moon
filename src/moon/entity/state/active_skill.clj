(ns moon.entity.state.active-skill
  (:require [moon.effect :as effect]
            [moon.effects.target-all :as target-all]
            [moon.effects.target-entity :as target-entity]
            [moon.entity :as entity]
            [moon.graphics :as graphics]
            [moon.timer :as timer]
            [moon.world.raycaster :as raycaster]))

(defn- update-effect-ctx
  [world {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? world @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(defmethod entity/tick :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
   eid
   {:keys [world/elapsed-time]
    :as world}]
  (let [effect-ctx (update-effect-ctx world effect-ctx)]
    (cond
     (not (seq (filter #(effect/applicable? % effect-ctx)
                       (:skill/effects skill))))
     [[:tx/event eid :action-done]]

     (timer/stopped? elapsed-time counter)
     [[:tx/effect effect-ctx (:skill/effects skill)]
      [:tx/event eid :action-done]])))

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

(defmethod entity/render :active-skill
  [[_k {:keys [skill effect-ctx counter]}]
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
                    effects))))
