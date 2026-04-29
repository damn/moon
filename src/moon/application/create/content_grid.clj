(ns moon.application.create.content-grid
  (:require [moon.content-grid :as content-grid]
            [moon.tiled-map :as tiled-map]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (tiled-map/width tiled-map)
                                                    (tiled-map/height tiled-map)
                                                    16)))
