(ns moon.effects.target-entity
  (:require [clojure.math.vector2 :as v]
            [moon.effect :as effect]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (v/add (:body/position body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))

(defn end-point [body target-body maxrange]
  (v/add (start-point body target-body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  maxrange)))

(defn in-range? [body target-body maxrange]
  (< (- (float (v/distance (:body/position body)
                           (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))

(defmethod effect/applicable? :effects/target-entity
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(effect/applicable? % effect-ctx) entity-effects))))

(defmethod effect/useful? :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _world]
  (in-range? (:entity/body @source)
             (:entity/body @target)
             maxrange))

(defmethod effect/handle :effects/target-entity
  [[_ {:keys [maxrange entity-effects]}]
   {:keys [effect/source effect/target] :as effect-ctx}
   _world]
  (let [body        (:entity/body @source)
        target-body (:entity/body @target)]
    (if (in-range? body target-body maxrange)
      [[:tx/spawn-line {:start (start-point body target-body)
                        :end (:body/position target-body)
                        :duration 0.05
                        :color [1 0 0 0.75]
                        :thick? true}]
       [:tx/effect effect-ctx entity-effects]]
      [[:tx/audiovisual
        (end-point body target-body maxrange)
        :audiovisuals/hit-ground]])))

(defmethod effect/render :effects/target-entity
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   _ctx]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (start-point body target-body)
        (end-point body target-body maxrange)
        (if (in-range? body target-body maxrange)
          [1 0 0 0.5]
          [1 1 0 0.5])]])))
