(ns clojure.tiled-tiled-map-tile-layer
  (:require [gdl.maps.tiled.tiled-map-tile-layer.cell :as tiled-map-tile-layer-cell]
            [gdl.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [gdl.maps.tiled.tiled-map :as tiled-map]
            [gdl.maps.map-properties :as map-properties]))

(defn f
  [{:keys [width
           height
           tilewidth
           tileheight
           name
           visible?
           map-properties
           tiles]}]
  {:pre [(string? name)
         (boolean? visible?)]}
  (let [layer (doto (tiled-map-tile-layer/new width height tilewidth tileheight)
                (tiled-map-tile-layer/set-name! name)
                (tiled-map-tile-layer/set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (map-properties/put! (tiled-map-tile-layer/get-properties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (tiled-map-tile-layer/set-cell! layer x y
                  (doto (tiled-map-tile-layer-cell/new)
                    (tiled-map-tile-layer-cell/set-tile! tile))))
    layer))
