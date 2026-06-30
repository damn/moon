(ns levelgen-test.draw-tiled-map
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/sprite-batch
           ctx/color-setter
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]}]
  (gdx/draw-tiled-map! sprite-batch
                       world-unit-scale
                       (:viewport/camera world-viewport)
                       tiled-map
                       color-setter))
