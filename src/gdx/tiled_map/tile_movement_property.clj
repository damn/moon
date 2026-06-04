(ns gdx.tiled-map.tile-movement-property
  (:require [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map-tile :as tile]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-name :refer [get-name]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer position)]
      (let [value (-> cell
                      cell/tile
                      tile/properties
                      (props-get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props-get (get-properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))
