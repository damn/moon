(ns moon.body
  (:require [moon.v2 :as v2]
            [moon.rectangle :as moon-rectangle]
            [com.badlogic.gdx.math.rectangle :as gdx-rectangle]))

(defn direction [body other-body]
  (v2/direction (:body/position body)
                (:body/position other-body)))

(defn start-point [body target-body]
  (v2/add (:body/position body)
          (v2/scale (direction body target-body)
                    (/ (:body/width body) 2))))

(defn end-point [body target-body maxrange]
  (v2/add (start-point body target-body)
          (v2/scale (direction body target-body)
                    maxrange)))

(defn in-range? [body target-body maxrange]
  (< (- (float (v2/distance (:body/position body)
                            (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))

(defn rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (gdx-rectangle/new x y width height)))

(defn overlaps? [body other-body]
  (gdx-rectangle/overlaps (rectangle body)
                          (rectangle other-body)))

(defn touched-tiles
  [{:keys [body/position
           body/width
           body/height]}]
  (moon-rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))
