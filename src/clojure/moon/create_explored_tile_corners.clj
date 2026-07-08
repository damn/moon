(ns clojure.moon.create-explored-tile-corners
  (:require [clojure.grid2d :as g2d]
            [clojure.map-properties :as map-properties]
            [clojure.tiled-map :as tiled-map]))

(defn f [ctx]
  (assoc ctx
         :ctx/explored-tile-corners (atom (g2d/create-grid (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "width")
                                                            (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "height")
                                                            (constantly false)))))
