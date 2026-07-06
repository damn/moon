(ns tiled-map.tile-movement-property
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer.get-cell :as get-cell]
            [clojure.gdx.tiled-map-tile-layer.get-name :as get-name]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell/f layer x y)]
      (let [value (map-properties/get (tiled-map-tile/get-properties (get-tile/f cell))
                         "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (map-properties/get (tiled-map/get-properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name/f layer) " is undefined."))
        value))))
