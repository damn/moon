(ns tiled-map.spawn-positions
  (:require [clojure.gdx.map-layers.get :as map-layers-get]
            [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map-tile-layer.get-cell :as get-cell]
            [clojure.gdx.tiled-map-tile-layer.get-height :as get-height]
            [clojure.gdx.tiled-map-tile-layer.get-width :as get-width]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]
            [clojure.gdx.tiled-map-tile.get-properties :as get-tile-properties]
            [clojure.gdx.tiled-map.get-layers :as get-layers]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (map-layers-get/f (get-layers/f tiled-map) layer-name)]
    (for [x (range (get-width/f layer))
          y (range (get-height/f layer))
          :let [position [x y]
                cell (get-cell/f layer x y)]
          :when cell
          :let [value (get/f (get-tile-properties/f (get-tile/f cell))
                            property-key)]
          :when value]
      [position value])))
