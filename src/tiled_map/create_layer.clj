(ns tiled-map.create-layer
  (:require [tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [^TiledMap tiled-map
   {:keys [name
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
