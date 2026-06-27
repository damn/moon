(ns tiled.tiled-map-tile-layer
  (:require [map-properties.put :as put!]
            [tiled-map-tile-layer.get-properties :as get-properties]
            [tiled-map-tile-layer.set-visible :refer [set-visible!]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as cell]))

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
  (let [layer (doto (layer/create width height tilewidth tileheight)
                (layer/set-name! name)
                (set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (put!/f (get-properties/f layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (layer/set-cell! layer
                       x
                       y
                       (doto (cell/create)
                         (cell/set-tile! tile))))
    layer))
