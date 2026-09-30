(ns moon.body
  (:require [gdx.rectangle :as gdx-rectangle]
            [moon.v2 :as v2]
            [moon.rectangle :as moon-rectangle]))

(defn direction [entity other-entity]
  (v2/direction (:entity/position entity)
                (:entity/position other-entity)))

(defn start-point [entity target-entity]
  (v2/add (:entity/position entity)
          (v2/scale (direction entity target-entity)
                    (/ (:entity/width entity) 2))))

(defn end-point [entity target-entity maxrange]
  (v2/add (start-point entity target-entity)
          (v2/scale (direction entity target-entity)
                    maxrange)))

(defn in-range? [entity target-entity maxrange]
  (< (- (float (v2/distance (:entity/position entity)
                            (:entity/position target-entity)))
        (float (/ (:entity/width entity)  2))
        (float (/ (:entity/width target-entity) 2)))
     (float maxrange)))

(defn rectangle
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (gdx-rectangle/create x y width height)))

(defn overlaps? [entity other-entity]
  (gdx-rectangle/overlaps (rectangle entity)
                          (rectangle other-entity)))

(defn touched-tiles
  [{:keys [entity/position
           entity/width
           entity/height]}]
  (moon-rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))
