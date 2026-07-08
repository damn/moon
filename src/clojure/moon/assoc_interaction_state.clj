(ns clojure.moon.assoc-interaction-state
  (:require [clojure.action-bar.selected-skill :as selected-skill]
            [clojure.scene2d.actor.mouseover-info :refer [mouseover-actor-info]]
            [clojure.body-distance :as distance]
            [clojure.scene2d.group :as group]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.player-effect-ctx :as player-effect-ctx]
            [clojure.usable-state :as usable-state]))

(defn- create
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor* (mouseover-actor ctx)]
    (cond
      mouseover-actor*
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor*)]

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
                            (#(group/find-actor % "moon.ui.action-bar"))
                            selected-skill/f)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx/f mouseover-eid world-mouse-position player-eid)
              state (usable-state/f skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn f [ctx]
  (assoc ctx :ctx/interaction-state (create ctx)))
