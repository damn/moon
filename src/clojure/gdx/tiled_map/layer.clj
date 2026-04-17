(ns clojure.gdx.tiled-map.layer
  (:refer-clojure :exclude [name])
  (:require [clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [clojure.gdx.maps.props :as props]
            [clojure.gdx.tiled-map.layer.cell :as cell]))

(def visible? layer/visible?)
(def cell layer/cell)
(def name layer/name)
(def properties layer/properties)
(def width layer/width)
(def height layer/height)

(defn create
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
    (props/put-all! (layer/properties layer) map-properties)
    (doseq [[xy tiled-map-tile] tiles
            :when tiled-map-tile]
      (layer/set-cell! layer
                       xy
                       (doto (cell/create)
                         (cell/set-tile! tiled-map-tile))))
    layer))
