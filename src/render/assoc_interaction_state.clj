(ns render.assoc-interaction-state
  (:require [moon.player-effect-ctx :as player-effect-ctx]
            [ctx.mouseover-actor :refer [mouseover-actor]]
            [moon.skill.usable-state :as usable-state]
            [render.assoc-interaction-state.mouseover-actor-info :refer [mouseover-actor-info]]
            [scene2d.group.find-actor :refer [find-actor]]
            [moon.action-bar.selected-skill :as selected-skill]
            [moon.body.distance :as distance]))

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
       :in-click-range? (< (distance/f (:entity/body @player-eid)
                                       (:entity/body @mouseover-eid))
                           (:entity/click-distance-tiles @player-eid))}]

     :else
     (if-let [skill-id (-> stage
                           :stage/root
                           (find-actor "moon.ui.action-bar")
                           selected-skill/f)]
       (let [entity @player-eid
             skill (skill-id (:entity/skills entity))
             effect-ctx (player-effect-ctx/f mouseover-eid world-mouse-position player-eid)
             state (usable-state/f skill entity effect-ctx)]
         (if (= state :usable)
           [:interaction-state.skill/usable [skill effect-ctx]]
           [:interaction-state.skill/not-usable state]))
       [:interaction-state/no-skill-selected]))))
