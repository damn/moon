(ns clojure.tiled-map.create-layer
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]
            [clojure.tiled-tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn f
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (tiled-map/getProperties tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (map-properties/get props "width")
      :height     (map-properties/get props "height")
      :tilewidth  (map-properties/get props "tilewidth")
      :tileheight (map-properties/get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))
