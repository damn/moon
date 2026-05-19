(ns game.resize.world-viewport
  (:require [gdl.utils.viewport :as viewport]))

(defn do!
  [{:keys [ctx/world-viewport]} width height]
  (viewport/update! world-viewport width height false))
