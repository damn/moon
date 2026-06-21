(ns render.assoc-interaction-state
  (:require [clojure.math.vector2.direction :as direction]
            [game.ctx.mouseover-actor :refer [mouseover-actor]]
            [game.skill :as skill]
            [render.assoc-interaction-state.mouseover-actor-info :refer [mouseover-actor-info]]
            [clojure.group.find-actor :refer [find-actor]]
            [moon.action-bar.selected-skill :as selected-skill]
            [moon.body :as body]))

(defn- player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (direction/f (:body/position (:entity/body @player-eid))
                                           target-position)}))

(defn create
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor (mouseover-actor ctx)]
    (cond
     mouseover-actor
     [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

     (and mouseover-eid
          (:entity/clickable @mouseover-eid))
     [:interaction-state/clickable-mouseover-eid
      {:clicked-eid mouseover-eid
       :in-click-range? (< (body/distance (:entity/body @player-eid)
                                          (:entity/body @mouseover-eid))
                           (:entity/click-distance-tiles @player-eid))}]

     :else
     (if-let [skill-id (-> stage
                           :stage/root
                           (find-actor "moon.ui.action-bar")
                           selected-skill/f)]
       (let [entity @player-eid
             skill (skill-id (:entity/skills entity))
             effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
             state (skill/usable-state skill entity effect-ctx)]
         (if (= state :usable)
           [:interaction-state.skill/usable [skill effect-ctx]]
           [:interaction-state.skill/not-usable state]))
       [:interaction-state/no-skill-selected]))))
