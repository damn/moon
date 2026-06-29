(ns levelgen-test.draw-tiled-map
  (:require [clojure.gdx.draw-tiled-map :as draw-tiled-map]))

(defn f
  [{:keys [ctx/sprite-batch
           ctx/color-setter
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]}]
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     color-setter))
