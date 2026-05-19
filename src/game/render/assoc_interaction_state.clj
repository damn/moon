(ns game.render.assoc-interaction-state
  (:require [gdl.scene2d.actor :as actor]
            [moon.stage :as stage]
            [gdl.app :as app]
            [gdl.input :as input]
            [clojure.math.vector2 :as v]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.body :as body]
            [moon.skill :as skill]
            [moon.ui.action-bar :as action-bar]))

(defn- button-class? [actor]
  (some #(= com.badlogic.gdx.scenes.scene2d.ui.Button %) (supers (class actor))))

(defn- button? [actor]
  (or (button-class? actor)
      (and (actor/parent actor)
           (button-class? (actor/parent actor)))))

; FIXME does not work
(defn- window-title-bar? [actor]
  (when (instance? com.badlogic.gdx.scenes.scene2d.ui.Label actor)
    (when-let [p (actor/parent actor)]
      (when-let [p (actor/parent p)]
        (and (instance? com.badlogic.gdx.scenes.scene2d.ui.Window actor)
             (= (window/title-label p) actor))))))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (button? actor)           [:mouseover-actor/button]
     :else                     [:mouseover-actor/unspecified])))

(defn- player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v/direction (:body/position (:entity/body @player-eid))
                                           target-position)}))


(defn- interaction-state
  [stage
   world-mouse-position
   mouseover-eid
   player-eid
   mouseover-actor]
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
                         (stage/find-actor "moon.ui.action-bar")
                         action-bar/selected-skill)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn step
  [{:keys [ctx/app
           ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (input/mouse-position (app/input app))))))
