(ns moon.application.resize
  (:require [moon.stage :as stage]
            [moon.world-viewport :as viewport]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width
   height]
  (stage/update-viewport! stage width height)
  (viewport/update! world-viewport width height false))
