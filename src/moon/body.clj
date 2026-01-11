(ns moon.body
  (:require [clojure.math.vector2 :as v]
            [moon.rectangle :as rectangle]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx.math Intersector
                                  Rectangle)))

(defn touched-tiles
  [{:keys [body/position body/width body/height]}]
  (rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))

(defn rectangle
  ;^Rectangle
  [{:keys [body/position body/width body/height]}]
  {:pre [position width height]}
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (Rectangle. x y width height)))

(defprotocol Body ; remove protocols not necessary ones
  (overlaps? [_ other-body])
  (distance [_ other-body])
  (direction [_ other-body]))

(q/defrecord RBody [body/position
                    body/width
                    body/height
                    body/collides?
                    body/z-order
                    body/rotation-angle]
  Body
  (overlaps? [body other-body]
    (Intersector/overlaps (rectangle body)
                          (rectangle other-body)))

  (distance [body other-body]
    (v/distance (:body/position body)
                (:body/position other-body)))

  (direction [body other-body]
    (v/direction (:body/position body)
                 (:body/position other-body))))

(defn create
  [{[x y] :position
    :keys [position
           width
           height
           collides?
           z-order
           rotation-angle]}
   {:keys [world/minimum-size
           world/z-orders]}]
  (assert position)
  (assert width)
  (assert height)
  (assert (>= width  (if collides? minimum-size 0)))
  (assert (>= height (if collides? minimum-size 0)))
  (assert (or (boolean? collides?) (nil? collides?)))
  (assert ((set z-orders) z-order))
  (assert (or (nil? rotation-angle)
              (<= 0 rotation-angle 360)))
  (map->RBody
   {:position (mapv float position)
    :width  (float width)
    :height (float height)
    :collides? collides?
    :z-order z-order
    :rotation-angle (or rotation-angle 0)}))
