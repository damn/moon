(ns create.grid
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [gdx.tiled-map.movement-property :as movement-property]
            [moon.cell :as cell]
            [moon.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (props/get (tiled-map/props tiled-map) "width")
                                        (props/get (tiled-map/props tiled-map) "height")
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (movement-property/f tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))
