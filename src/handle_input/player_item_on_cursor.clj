(ns handle-input.player-item-on-cursor
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [clojure.gdx.input.buttons :as input.buttons]
            [game.ctx.mouseover-actor :refer [mouseover-actor]]))

(defn f
  [eid {:keys [ctx/app] :as ctx}]
  (when (and (input/button-just-pressed? (app/input app) input.buttons/left)
             (not (mouseover-actor ctx)))
    [[:tx/event eid :drop-item]]))
