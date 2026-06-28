(ns tiled-map.create-layer
  (:require [tiled-map.get-properties :as get-properties]
            [tiled.tiled-map-tile-layer :as tiled-map-tile-layer])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (get-properties/f tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (MapProperties/.get props "width")
      :height     (MapProperties/.get props "height")
      :tilewidth  (MapProperties/.get props "tilewidth")
      :tileheight (MapProperties/.get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))
