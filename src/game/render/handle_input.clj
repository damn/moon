(ns game.render.handle-input
  (:require [moon.controls :as controls]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.stage :as stage]
            [clojure.input.buttons :as input.buttons]
            [clojure.app :as app]
            [clojure.input :as input]
            [moon.inventory :as inventory]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.txs :as txs]))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defmethod state/handle-input :player-moving
  [_ eid ctx]
  (if-let [movement-vector (controls/player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (-> stage
                 (stage/find-actor "moon.ui.windows.inventory")
                 actor/visible?)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(defmethod state/handle-input :player-idle
  [_ player-eid {:keys [ctx/app
                        ctx/interaction-state
                        ctx/stage] :as ctx}]
  (if-let [movement-vector (controls/player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/button-just-pressed? (app/input app) input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))


(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)
