(ns moon.entity.body
  (:require [moon.math.geom :as geom]
            [clojure.math.vector2 :as v]
            [qrecord.core :as q]))

(defprotocol Body
  (overlaps? [_ other-body])
  (touched-tiles [_])
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
    (geom/overlaps? (geom/body->gdx-rectangle body)
                    (geom/body->gdx-rectangle other-body)))

  (touched-tiles [body]
    (geom/body->touched-tiles body))

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
