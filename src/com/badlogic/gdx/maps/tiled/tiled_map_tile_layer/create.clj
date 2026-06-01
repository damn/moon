(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.create
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

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
                (layer/set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (props/put! (layer/properties layer) k v))
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (cell/create tile)))
    layer))
