(ns moon.game.render.assoc-active-entities
  (:require [moon.world :as world]))

(defn step
  [{:keys [ctx/world]
    :as ctx}]
  (update ctx :ctx/world world/cache-active-entities))
