(ns levelgen-test.resize
  (:require [gdx.utils.viewport :as viewport]))

(defn f!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))
