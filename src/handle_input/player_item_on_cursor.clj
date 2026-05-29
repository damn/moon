(ns handle-input.player-item-on-cursor
  (:require [gdx.input.buttons :as input.buttons]
            [gdx.scenes.scene2d.stage :as stage]
            [game.ctx :as ctx]
            [game.state :as state]))

(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/stage]
          :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))]
    (when (and (ctx/button-just-pressed? ctx input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))
