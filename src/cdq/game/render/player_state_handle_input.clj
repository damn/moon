(ns cdq.game.render.player-state-handle-input
  (:require [cdq.entity.inventory :as inventory]
            [cdq.entity.stats :as stats]
            [cdq.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [cdq.input :as input]
            [cdq.ui :as ui]
            [clojure.gdx.input.buttons :as input.buttons]
            [moon.txs :as txs]))

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
             (ui/inventory-window-visible? stage)
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

(let [fn-map {:player-idle           (fn
                                       [player-eid
                                        {:keys [ctx/input
                                                ctx/interaction-state
                                                ctx/stage] :as ctx}]
                                       (if-let [movement-vector (input/player-movement-vector input)]
                                         [[:tx/event player-eid :movement-input movement-vector]]
                                         (when (input/button-just-pressed? input input.buttons/left)
                                           (interaction-state->txs interaction-state
                                                                   stage
                                                                   player-eid))))

              :player-item-on-cursor (fn
                                       [eid {:keys [ctx/input
                                                    ctx/stage]}]
                                       (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))]
                                         (when (and (input/button-just-pressed? input input.buttons/left)
                                                    (player-item-on-cursor/world-item? mouseover-actor))
                                           [[:tx/event eid :drop-item]])))

              :player-moving         (let [speed (fn [{:keys [entity/stats]}]
                                                   (or (stats/get-stat-value stats :stats/movement-speed)
                                                       0))]
                                       (fn [eid {:keys [ctx/input]}]
                                         (if-let [movement-vector (input/player-movement-vector input)]
                                           [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                             :speed (speed @eid)}]]
                                           [[:tx/event eid :no-movement-input]])))}]
  (defn- state->handle-input [[k v] eid ctx]
    (if-let [f (k fn-map)]
      (f eid ctx)
      nil)))

(defn step
  [{:keys [ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state->handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)
