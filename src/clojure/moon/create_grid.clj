(ns clojure.moon.create-grid
  (:require [clojure.grid-cell :as grid-cell]
            [clojure.grid2d :as g2d]
            [gdl.maps.map-properties :as map-properties]
            [clojure.movement-property :as movement-property]
            [gdl.maps.tiled.tiled-map :as tiled-map]))

(defn f [ctx]
  (assoc ctx
         :ctx/grid (g2d/create-grid (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "width")
                                    (map-properties/get (tiled-map/get-properties (:ctx/tiled-map ctx)) "height")
                                    (fn [position]
                                      (atom
                                       (grid-cell/map->R
                                        {:position position
                                         :middle (mapv (partial + 0.5) position)
                                         :movement (case (movement-property/f (:ctx/tiled-map ctx) position)
                                                      "none" :none
                                                      "air" :air
                                                      "all" :all)
                                         :entities #{}
                                         :occupied #{}}))))))
