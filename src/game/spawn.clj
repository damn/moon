(ns game.spawn
  (:require [clojure.math :as math]
            [moon.m :as m]
            [moon.timer :as timer]
            [moon.v2 :as v2]))

(defn item-place-position [player-position world-mouse-position maxrange]
  (mapv + player-position
        (v2/scale (v2/direction player-position world-mouse-position)
                  (min maxrange
                       (v2/distance player-position world-mouse-position)))))

(defn spawn-creature [{:keys [position creature-property components]}]
  (assert creature-property)
  (-> creature-property
      (assoc :entity/position position
             :entity/collides? true
             :entity/z-order (if (:entity/flying? creature-property)
                               :z-order/flying
                               :z-order/ground))
      (assoc :entity/destroy-audiovisual :audiovisuals/creature-die)
      (m/safe-merge components)))

(defn spawn-effect [position components]
  (assoc components
         :entity/width 0.5
         :entity/height 0.5
         :entity/z-order :z-order/effect
         :entity/position position))

(defn spawn-alert [position faction duration elapsed-time]
  (spawn-effect position
                {:entity/alert-friendlies-after-duration
                 {:counter (timer/create elapsed-time duration)
                  :faction faction}}))

(defn spawn-item [position item]
  {:entity/position position
   :entity/width 0.75
   :entity/height 0.75
   :entity/z-order :z-order/on-ground
   :entity/image (:entity/image item)
   :entity/item item
   :entity/clickable {:type :clickable/item
                      :text (:property/pretty-name item)}})

(defn spawn-line [{:keys [start end duration color thick?]}]
  (spawn-effect start
                {:entity/line-render {:thick? thick? :end end :color color}
                 :entity/delete-after-duration duration}))

(defn spawn-projectile
  [{:keys [position direction faction]}
   {:keys [entity/image
           projectile/max-range
           projectile/speed
           entity-effects
           projectile/size
           projectile/piercing?]}]
  {:entity/position position
   :entity/width size
   :entity/height size
   :entity/z-order :z-order/flying
   :entity/rotation-angle (let [angle (math/to-degrees
                                       (math/atan2 (v2/crs [0 1] direction)
                                                   (v2/dot [0 1] direction)))]
                            (if (neg? angle)
                              (+ angle 360)
                              angle))
   :entity/movement {:direction direction :speed speed}
   :entity/image image
   :entity/faction faction
   :entity/delete-after-duration (/ max-range speed)
   :entity/destroy-audiovisual :audiovisuals/hit-wall
   :entity/projectile-collision {:entity-effects entity-effects
                                 :piercing? piercing?}})
