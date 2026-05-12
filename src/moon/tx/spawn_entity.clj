(ns moon.tx.spawn-entity
  (:require [clojure.animation :as animation]
            [clojure.math.rectangle :as rectangle]
            [clojure.math.vector2 :as v]
            [moon.body :as body]
            [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.grid2d :as g2d]
            [moon.inventory :as inventory]
            [moon.skill]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [qrecord.core :as q]
            [reduce-fsm :as fsm])
  (:import (com.badlogic.gdx.math Rectangle)))

(comment

 ; 1. quote the data structur ebecause of arrows
 ; 2.
 (eval `(fsm/fsm-inc ~data))
 )

(def ^:private npc-fsm
  (fsm/fsm-inc
   [[:npc-sleeping
     :kill -> :npc-dead
     :stun -> :stunned
     :alert -> :npc-idle]
    [:npc-idle
     :kill -> :npc-dead
     :stun -> :stunned
     :start-action -> :active-skill
     :movement-direction -> :npc-moving]
    [:npc-moving
     :kill -> :npc-dead
     :stun -> :stunned
     :timer-finished -> :npc-idle]
    [:active-skill
     :kill -> :npc-dead
     :stun -> :stunned
     :action-done -> :npc-idle]
    [:stunned
     :kill -> :npc-dead
     :effect-wears-off -> :npc-idle]
    [:npc-dead]]))

(def ^:private player-fsm
  (fsm/fsm-inc
   [[:player-idle
     :kill -> :player-dead
     :stun -> :stunned
     :start-action -> :active-skill
     :pickup-item -> :player-item-on-cursor
     :movement-input -> :player-moving]
    [:player-moving
     :kill -> :player-dead
     :stun -> :stunned
     :no-movement-input -> :player-idle]
    [:active-skill
     :kill -> :player-dead
     :stun -> :stunned
     :action-done -> :player-idle]
    [:stunned
     :kill -> :player-dead
     :effect-wears-off -> :player-idle]
    [:player-item-on-cursor
     :kill -> :player-dead
     :stun -> :stunned
     :drop-item -> :player-idle
     :dropped-item -> :player-idle]
    [:player-dead]]))

(defmethod entity/create :entity/animation
  [[_k {:keys [animation/frames
               animation/frame-duration
               animation/looping?
               delete-after-stopped?]}]
   _ctx]
  (assert (not (and looping? delete-after-stopped?)))
  (animation/map->RAnimation
   {:frames (vec frames)
    :frame-duration frame-duration
    :looping? looping?
    :cnt 0
    :maxcnt (* (count frames) (float frame-duration))
    :delete-after-stopped? delete-after-stopped?}))

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

(defmethod entity/create :entity/body
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

(defmethod entity/create :entity/delete-after-duration
  [[_ duration] {:keys [ctx/elapsed-time]}]
  (timer/create elapsed-time duration))

(defmethod entity/create :entity/projectile-collision
  [[_ v] _ctx]
  (assoc v :already-hit-bodies #{}))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))

(defmethod entity/after-create :entity/fsm ; TODO do @ creature?
  [[_k {:keys [fsm initial-state]}] eid ctx]
  ; fsm throws when initial-state is not part of states, so no need to assert initial-state
  ; initial state is nil, so associng it. make bug report at reduce-fsm?
  [[:tx/assoc eid :entity/fsm (assoc ((case fsm
                                        :fsms/player player-fsm
                                        :fsms/npc npc-fsm) initial-state nil)
                                     :state initial-state)]
   [:tx/assoc eid initial-state (state/create [initial-state nil] eid ctx)]])

(defmethod entity/after-create :entity/inventory ; TODO do @ creature
  [[_k items] eid _ctx]
  (cons [:tx/assoc eid :entity/inventory (->> inventory/empty-inventory
                                              (map (fn [[slot [width height]]]
                                                     [slot (g2d/create-grid width height (constantly nil))]))
                                              (into {}))]
        (for [item items] ; TODO just call on inventory itself? -> and callback player-refresh ?
          [:tx/pickup-item eid item])))

(defmethod entity/after-create :entity/skills ; TODO same like inventory ?
  [[_k skills] eid _ctx]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(.bindRoot #'moon.skill/usable-state
           (fn [{:keys [skill/cooling-down? skill/effects] :as skill}
                entity
                effect-ctx]
             (cond
              cooling-down?
              :cooldown

              (stats/not-enough-mana? (:entity/stats entity) skill)
              :not-enough-mana

              (not (seq (filter #(effect/applicable? % effect-ctx) effects)))
              :invalid-params

              :else
              :usable)))

(q/defrecord Entity [entity/body])

(defn do! [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (entity/create [k v] ctx)))
                       {}
                       entity)
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]
    (cons
     [:tx/register-eid eid]
     (mapcat #(entity/after-create % eid ctx) @eid))))
