(ns ctx.grid
  (:require [tiled-map.movement-property :as movement-property]
            [clojure.grid2d :as g2d]
            [moon.records.grid-cell :as grid-cell])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (.get (TiledMap/.getProperties tiled-map) "width")
                   (.get (TiledMap/.getProperties tiled-map) "height")
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
