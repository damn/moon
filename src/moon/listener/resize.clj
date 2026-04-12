(ns moon.listener.resize
  (:require [gdl.viewport :as viewport]))

(defn do!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)
