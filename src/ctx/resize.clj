(ns ctx.resize
  (:require [clojure.gdx.viewport.update :as update-viewport]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (update-viewport/f (:stage/viewport stage) width height true)
  (update-viewport/f world-viewport width height false))
