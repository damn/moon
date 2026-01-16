(ns moon.render.player-state
  (:require [clojure.math.vector2 :as v]
            [moon.ctx :as ctx]
            [moon.body :as body]
            [moon.entity.state :as state]
            [moon.input :as input]
            [moon.skill :as skill]
            [moon.stage :as stage]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.utils :as ui-utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx Graphics)))

(defn- inventory-cell-with-item? [^Actor actor]
  (and (.getParent actor)
       (= "inventory-cell" (.getName (.getParent actor)))
       (.getUserObject (.getParent actor))))

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
   [:interaction-state/mouseover-actor (let [actor mouseover-actor
                                             inventory-slot (inventory-cell-with-item? actor)]
                                         (cond
                                          inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
                                          (ui-utils/window-title-bar? actor) [:mouseover-actor/window-title-bar]
                                          (ui-utils/button?           actor) [:mouseover-actor/button]
                                          :else                     [:mouseover-actor/unspecified]))]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (-> stage
                         .getRoot
                         (.findActor "moon.ui.action-bar")
                         action-bar/selected-skill)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn- assoc-interaction-state
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (input/mouse-position input)))))

(defn- set-cursor
  [{:keys [ctx/cursors
           ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (Graphics/.setCursor graphics (get cursors cursor-key)))
  ctx)

(defn- player-state-handle-input
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (ctx/handle! ctx txs))
  ctx)

(defn- dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(defn do! [ctx]
  (-> ctx
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      dissoc-interaction-state))
