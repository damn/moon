(ns moon.game.render.set-cursor
  (:require [moon.graphics :as graphics]))

(defn- player-idle->cursor [player-eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @player-eid) inventory-slot)))]
        (cond
          inventory-cell-with-item?
          :cursors/hand-before-grab

          (= actor-type :mouseover-actor/window-title-bar)
          :cursors/move-window

          (= actor-type :mouseover-actor/button)
          :cursors/over-button

          (= actor-type :mouseover-actor/unspecified)
          :cursors/default

          :else
          :cursors/default))

      :interaction-state/clickable-mouseover-eid
      (let [{:keys [clicked-eid
                    in-click-range?]} params]
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/item (if in-click-range?
                            :cursors/hand-before-grab
                            :cursors/hand-before-grab-gray)
          :clickable/player :cursors/bag))

      :interaction-state.skill/usable
      :cursors/use-skill

      :interaction-state.skill/not-usable
      :cursors/skill-not-usable

      :interaction-state/no-skill-selected
      :cursors/no-skill-selected)))

(let [fn-map {:active-skill :cursors/sandclock
              :player-dead :cursors/black-x
              :player-idle player-idle->cursor
              :player-item-on-cursor :cursors/hand-grab
              :player-moving :cursors/walking
              :stunned :cursors/denied}]
  (defn state->cursor [[k v] eid ctx]
    (let [->cursor (k fn-map)]
      (if (keyword? ->cursor)
        ->cursor
        (->cursor eid ctx)))))

(defn step
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state->cursor [state-k (state-k entity)] eid ctx)]
    (graphics/set-cursor! graphics cursor-key))
  ctx)
