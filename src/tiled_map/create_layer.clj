(ns tiled-map.create-layer
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties] [tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn f
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (get-properties/f tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (map-properties/get props "width")
      :height     (map-properties/get props "height")
      :tilewidth  (map-properties/get props "tilewidth")
      :tileheight (map-properties/get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))
