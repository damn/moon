(ns moon.application.render.handle-input
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.buttons :as input.buttons]
            [moon.input]
            [moon.inventory :as inventory]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.txs :as txs]))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defmethod state/handle-input :player-moving
  [_ eid {:keys [ctx/input]}]
  (if-let [movement-vector (moon.input/player-movement-vector input)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))

(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/input
                 ctx/stage]}]
  (let [mouseover-actor (stage/mouseover-actor stage (input/mouse-position input))]
    (when (and (input/button-just-pressed? input input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))

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
  [_ player-eid {:keys [ctx/input
                        ctx/interaction-state
                        ctx/stage] :as ctx}]
  (if-let [movement-vector (moon.input/player-movement-vector input)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/button-just-pressed? input input.buttons/left)
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
