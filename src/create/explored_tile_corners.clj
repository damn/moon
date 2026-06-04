(ns create.explored-tile-corners
  (:require [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :refer [get-properties]]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/explored-tile-corners
         (atom (g2d/create-grid (props-get (get-properties tiled-map) "width")
                                (props-get (get-properties tiled-map) "height")
                                (constantly false)))))
