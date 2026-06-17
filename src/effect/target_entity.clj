(ns effect.target-entity
  (:require [moon.body.in-range :refer [in-range?]]
            [moon.body.start-point :refer [start-point]]
            [moon.body.end-point :refer [end-point]]
            [game.effect :as effect]))

(defmethod effect/applicable? :effects/target-entity
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(effect/applicable? % effect-ctx) entity-effects))))

(defmethod effect/useful? :effects/target-entity
  [[_ {:keys [maxrange]}] {:keys [effect/source effect/target]} _ctx]
  (in-range? (:entity/body @source)
             (:entity/body @target)
             maxrange))

(defmethod effect/handle :effects/target-entity
  [[_ {:keys [maxrange entity-effects]}]
   {:keys [effect/source effect/target] :as effect-ctx}
   {:keys [ctx/colors]}]
  (let [body        (:entity/body @source)
        target-body (:entity/body @target)]
    (if (in-range? body target-body maxrange)
      [[:tx/spawn-line {:start (start-point body target-body)
                        :end (:body/position target-body)
                        :duration 0.05
                        :color (:colors/target-entity-line colors)
                        :thick? true}]
       [:tx/effect effect-ctx entity-effects]]
      [[:tx/audiovisual
        (end-point body target-body maxrange)
        :audiovisuals/hit-ground]])))

(defmethod effect/render :effects/target-entity
  [[_ {:keys [maxrange]}]
   {:keys [effect/source effect/target]}
   {:keys [ctx/colors]}]
  (when target
    (let [body        (:entity/body @source)
          target-body (:entity/body @target)]
      [[:draw/line
        (start-point body target-body)
        (end-point body target-body maxrange)
        (if (in-range? body target-body maxrange)
          (:colors/target-entity-in-range colors)
          (:colors/target-entity-not-in-range colors))]])))
