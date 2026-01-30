(ns moon.entity.body
  (:require [qrecord.core :as q]))

; TODO is a body really necessary? just entity/foo ?
; pull stuff out which is not used together w. body functions ?
(q/defrecord Body [body/position
                   body/width
                   body/height
                   body/collides?
                   body/z-order
                   body/rotation-angle])


(defn create
  [[_k
    {[x y] :position
     :keys [position
            width
            height
            collides?
            z-order
            rotation-angle]}]
   {:keys [ctx/minimum-size
           ctx/z-orders]}]
  (assert position)
  (assert width)
  (assert height)
  (assert (>= width  (if collides? minimum-size 0)))
  (assert (>= height (if collides? minimum-size 0)))
  (assert (or (boolean? collides?) (nil? collides?)))
  (assert ((set z-orders) z-order))
  (assert (or (nil? rotation-angle)
              (<= 0 rotation-angle 360)))
  (map->Body
   {:position (mapv float position)
    :width  (float width)
    :height (float height)
    :collides? collides?
    :z-order z-order
    :rotation-angle (or rotation-angle 0)}))
