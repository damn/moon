(ns ctx.explored-tile-corners
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (map-properties/get (tiled-map/get-properties tiled-map) "width")
                         (map-properties/get (tiled-map/get-properties tiled-map) "height")
                         (constantly false))))
