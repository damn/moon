(ns com.badlogic.gdx.maps.tiled.tiled-map.add-layer
  (:require [com.badlogic.gdx.maps.map-layers :as layers]
            [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.create :as create-layer]))

(defn- create-layer*
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (tiled-map/props tiled-map)]
    (create-layer/f {:width      (props/get props "width")
                     :height     (props/get props "height")
                     :tilewidth  (props/get props "tilewidth")
                     :tileheight (props/get props "tileheight")
                     :name name
                     :visible? visible?
                     :map-properties properties
                     :tiles tiles})))

(defn f [tiled-map layer]
  (layers/add! (tiled-map/layers tiled-map)
               (create-layer* tiled-map layer)))
