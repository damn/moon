(ns game.ctx.resize
  (:require [clojure.utils.viewport.update :as update!]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (update!/f (:stage/viewport stage) width height true)
  (update!/f world-viewport width height false))
