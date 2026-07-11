(ns moon.body
  (:require [clojure.v2.add :as add]
            [clojure.v2.direction :as direction]
            [clojure.v2.scale :as scale]
            [clojure.v2.distance :as distance]
            [clojure.start-point :refer [start-point]]
            [moon.rectangle :as moon-rectangle]
            [gdl.math.rectangle :as rectangle]
            [com.badlogic.gdx.math.rectangle :as gdx-rectangle]))

(defn direction [body other-body]
  (direction/f (:body/position body)
               (:body/position other-body)))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))

(defn in-range? [body target-body maxrange]
  (< (- (float (distance/f (:body/position body)
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

(defn touched-tiles
  [{:keys [body/position
           body/width
           body/height]}]
  (moon-rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))
