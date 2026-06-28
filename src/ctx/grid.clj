(ns ctx.grid
  (:require [tiled-map.get-properties :as get-properties]
            [tiled-map.movement-property :as movement-property]
            [clojure.grid2d :as g2d]
            [moon.records.grid-cell :as grid-cell])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (MapProperties/.get (get-properties/f tiled-map) "width")
                   (MapProperties/.get (get-properties/f tiled-map) "height")
                   (fn [position]
                     (atom
                      (grid-cell/map->R
                       {:position position
                        :middle (mapv (partial + 0.5) position)
                        :movement (case (movement-property/f tiled-map position)
                                    "none" :none
                                    "air"  :air
                                    "all"  :all)
                        :entities #{}
                        :occupied #{}})))))
