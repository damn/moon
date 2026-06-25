(ns tiled-map.create-layer
  (:require [map-properties.get :as get]
            [tiled-map.get-properties :as get-properties]
            [tiled.tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn f
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (get-properties/f tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (get/f props "width")
      :height     (get/f props "height")
      :tilewidth  (get/f props "tilewidth")
      :tileheight (get/f props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))
