(ns levelgen-test.resize
  (:require [gdl.viewport.update :as update!]))

(defn f!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (update!/f (:stage/viewport stage) width height true)
  (update!/f world-viewport width height false))
