(ns com.badlogic.gdx.maps.tiled.tiled-map.add-layer
  (:require [com.badlogic.gdx.maps.layers.add :refer [add!]]
            [com.badlogic.gdx.maps.properties.get :refer [props-get]]
            [com.badlogic.gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [com.badlogic.gdx.maps.get-properties :refer [get-properties]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.create :as create-layer]))

(defn- create-layer*
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (get-properties tiled-map)]
    (create-layer/f {:width      (props-get props "width")
                     :height     (props-get props "height")
                     :tilewidth  (props-get props "tilewidth")
                     :tileheight (props-get props "tileheight")
                     :name name
                     :visible? visible?
                     :map-properties properties
                     :tiles tiles})))

(defn f [tiled-map layer]
  (add! (get-layers tiled-map)
        (create-layer* tiled-map layer)))
