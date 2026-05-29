(ns application.resize
  (:require [gdx.utils.viewport.viewport :as viewport]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))
