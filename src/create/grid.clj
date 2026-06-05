(ns create.grid
  (:require [clojure.maps.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [gdx.tiled-map.movement-property :as movement-property]
            [moon.cell :as cell]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (props-get (get-properties tiled-map) "width")
                                        (props-get (get-properties tiled-map) "height")
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (movement-property/f tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))
