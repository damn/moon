(ns gdl.add-layer
  (:require [map-layers.add :as add!]
            [map-properties.get :as get]
            [gdl.get-layers :refer [get-layers]]
            [tiled-map.get-properties :as get-properties]
            [gdl.tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn- create-layer*
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

(defn f [tiled-map layer]
  (add!/f (get-layers tiled-map)
          (create-layer* tiled-map layer)))
