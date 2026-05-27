(ns moon.entity
  (:require [clojure.animation :as animation]
            [clojure.math.rectangle :as rectangle]
            [clojure.math.vector2 :as v]
            [moon.body :as body]
            [moon.grid :as grid]
            [moon.timer :as timer]
            [qrecord.core :as q])
  (:import (com.badlogic.gdx.math Rectangle)))

(defmulti create
  (fn [[k _v] _ctx]
    k))

(defmethod create :default
  [[_ v] _ctx]
  v)

(defmulti after-create
  (fn [[k _v] _eid _ctx]
    k))

(defmethod after-create :default
  [[_k _v] _eid _ctx]
  nil)

(defmulti destroy
  (fn [[k _v] _eid]
    k))

(defmethod destroy :default
  [[_k _v] _eid]
  nil)

(defmulti tick
  (fn [[k _v] _eid _ctx]
    k))

(defmethod tick :default
  [[_k _v] _eid _ctx]
  nil)

(defmulti render
  (fn [[k _v] _entity _ctx]
    k))

(q/defrecord Body [body/position
                   body/width
                   body/height
                   body/collides?
                   body/z-order
                   body/rotation-angle]
  body/Body
  (rectangle
    [{:keys [body/position
             body/width
             body/height]}]
    (let [[x y] [(- (position 0) (/ width  2))
                 (- (position 1) (/ height 2))]]
      (Rectangle. x y width height)))

  (touched-tiles
    [{:keys [body/position
             body/width
             body/height]}]
    (rectangle/touched-tiles
     {:x (- (position 0) (/ width  2))
      :y (- (position 1) (/ height 2))
      :width  width
      :height height}))

  (overlaps? [body other-body]
    (.overlaps ^Rectangle (body/rectangle body)
               ^Rectangle (body/rectangle other-body)))

  (distance [body other-body]
    (v/distance (:body/position body)
                (:body/position other-body)))

  (direction [body other-body]
    (v/direction (:body/position body)
                 (:body/position other-body))))

(defmethod create :entity/body
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

(defmethod render :entity/clickable
  [[_k {:keys [text]}]
   {:keys [entity/body
           entity/mouseover?]}
   _ctx]
  (when (and mouseover? text)
    (let [[x y] (:body/position body)]
      [[:draw/text {:text text
                    :x x
                    :y (+ y (/ (:body/height body) 2))
                    :up? true}]])))

(defmethod create :entity/animation
  [[_k {:keys [animation/frames
               animation/frame-duration
               animation/looping?
               delete-after-stopped?]}]
   _ctx]
  (assert (not (and looping? delete-after-stopped?)))
  {:frames (vec frames)
   :frame-duration frame-duration
   :looping? looping?
   :cnt 0
   :maxcnt (* (count frames) (float frame-duration))
   :delete-after-stopped? delete-after-stopped?})

(defmethod tick :entity/animation
  [[_k animation] eid {:keys [ctx/delta-time]}]
  [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
   (when (and (:delete-after-stopped? animation)
              (animation/stopped? animation))
     [:tx/mark-destroyed eid])])

(defmethod render :entity/animation
  [[_k animation] entity ctx]
  (render [:entity/image (animation/current-frame animation)]
          entity
          ctx))

(defmethod tick :entity/alert-friendlies-after-duration
  [[_k {:keys [counter faction]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/grid]}]
  (when (timer/stopped? elapsed-time counter)
    (cons [:tx/mark-destroyed eid]
          (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                   :radius 4}
                                  (grid/circle->entities grid)
                                  (filter #(= (:entity/faction @%) faction)))]
            [:tx/event friendly-eid :alert]))))
