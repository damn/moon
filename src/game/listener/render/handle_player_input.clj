(ns game.listener.render.handle-player-input
  (:require [game.entity :refer [set-item]]
            [audio.play :refer [play-sound!]]
            [game.fsm :refer [handle-fsm-event!]]
            [game.mouse :refer [mouseover-actor ui-mouse-position world-mouse-position]]
            [game.ui :refer [toggle-inventory-visible! ui-set-item!]]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.stats :as stats]
            [moon.v2 :as v2])
  (:import (com.badlogic.gdx Input$Buttons Input$Keys)
           (com.badlogic.gdx.scenes.scene2d Stage)))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce #(mapv + %1 %2) [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn- show-message! [stage message]
  (-> (.getRoot ^Stage stage)
      (.findActor "player-message")
      (.setUserObject (atom {:text message :counter 0}))))

(defn- interaction-state->txs [[k params] world elapsed-time stage audio skin textures z-orders minimum-size ui-set-item! player-eid world-mouse-position]
  (case k
    :interaction-state/mouseover-actor
    nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          (do (toggle-inventory-visible! stage)
              nil)

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
              (-> (.getRoot ^Stage stage)
                  (.findActor "moon.ui.windows.inventory")
                  .isVisible)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_takeit.wav")
                  (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size player-eid world-mouse-position :pickup-item item))

              (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
              (do (swap! clicked-eid assoc :entity/destroyed? true)
                  (play-sound! audio "bfxr_pickup.wav")
                  (assert (item/valid? item))
                  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @player-eid) item)]
                    (assert cell)
                    (assert (nil? cell-item))
                    (swap! player-eid set-item cell item)
                    (ui-set-item! cell item))
                  nil)

              :else
              (do (play-sound! audio "bfxr_denied.wav")
                  (show-message! stage "Your Inventory is full")
                  nil))))
        (do (play-sound! audio "bfxr_denied.wav")
            (show-message! stage "Too far away")
            nil)))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size player-eid world-mouse-position :start-action [skill effect-ctx]))

    :interaction-state.skill/not-usable
    (let [state params]
      (do (play-sound! audio "bfxr_denied.wav")
          (show-message! stage (case state
                                 :cooldown "Skill is still on cooldown"
                                 :not-enough-mana "Not enough mana"
                                 :invalid-params "Cannot use this here"))
          nil))

    :interaction-state/no-skill-selected
    (do (play-sound! audio "bfxr_denied.wav")
        (show-message! stage "No selected skill")
        nil)))

(defn handle-player-input!
  [world elapsed-time interaction-state player-eid
   audio skin stage textures z-orders minimum-size world-viewport key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        left-button-pressed? (button-just-pressed? Input$Buttons/LEFT)
        move-v (movement-vector key-pressed?)
        [x y] (ui-mouse-position stage)
        mouseover-actor* (mouseover-actor stage x y)
        world-mouse-pos (world-mouse-position world-viewport)]
    (case state-k
      :player-idle
      (if move-v
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :movement-input move-v)
        (when left-button-pressed?
          (interaction-state->txs @interaction-state
                                  world
                                  elapsed-time
                                  stage
                                  audio
                                  skin
                                  textures
                                  z-orders
                                  minimum-size
                                  #(ui-set-item! skin stage textures %1 %2)
                                  eid
                                  world-mouse-pos)))

      :player-moving
      (if move-v
        (do (swap! eid assoc :entity/movement {:direction move-v
                                               :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                                          0)})
            nil)
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :no-movement-input))

      :player-item-on-cursor
      (when (and left-button-pressed?
                 (not mouseover-actor*))
        (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-pos :drop-item))

      nil)))