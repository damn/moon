(ns ctx.resize
  (:require [clojure.gdx :as gdx]))

(defn do!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (gdx/viewport-update (:stage/viewport stage) width height true)
  (gdx/viewport-update world-viewport width height false))
