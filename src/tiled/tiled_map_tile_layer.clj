(ns tiled.tiled-map-tile-layer
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer.get-properties :as get-properties]
            [clojure.gdx.tiled-map-tile-layer.new :as new-layer]
            [clojure.gdx.tiled-map-tile-layer.set-cell :as set-cell]
            [clojure.gdx.tiled-map-tile-layer.set-name :as set-name]
            [clojure.gdx.tiled-map-tile-layer.set-visible :as set-visible]
            [clojure.gdx.tiled-map-tile-layer$cell.new :as new-cell]
            [clojure.gdx.tiled-map-tile-layer$cell.set-tile :as set-tile]))

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
  (let [layer (doto (new-layer/f width height tilewidth tileheight)
                (set-name/f name)
                (set-visible/f visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (map-properties/put! (get-properties/f layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (set-cell/f layer x y
                  (doto (new-cell/f)
                    (set-tile/f tile))))
    layer))
