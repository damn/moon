(ns moon.impl.content-grid
  (:require [moon.content-grid :as content-grid]
            [moon.tiled-map :as tiled-map]))

(defn create
  [{:keys [ctx/tiled-map]}]
  (content-grid/create (tiled-map/width tiled-map)
                       (tiled-map/height tiled-map)
                       16))
