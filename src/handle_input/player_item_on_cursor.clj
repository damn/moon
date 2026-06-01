(ns handle-input.player-item-on-cursor
  (:require [com.badlogic.gdx.input.buttons :as input.buttons]
            [gdx.stage :as stage]
            [game.ctx :as ctx]))

(defn f
  [eid {:keys [ctx/stage]
        :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))]
    (when (and (ctx/button-just-pressed? ctx input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))
