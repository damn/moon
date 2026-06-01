(ns handle-input.player-item-on-cursor
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.input.buttons :as input.buttons]
            [game.ctx.mouse-position :refer [mouse-position]]
            [gdx.stage :as stage]))

(defn f
  [eid {:keys [ctx/app
               ctx/stage]
        :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (mouse-position ctx))]
    (when (and (input/button-just-pressed? (app/input app) input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))
