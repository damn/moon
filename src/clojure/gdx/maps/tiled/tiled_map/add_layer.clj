(ns clojure.gdx.maps.tiled.tiled-map.add-layer
  (:require [clojure.gdx.maps.map-layers :as layers]
            [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.create :as create-layer]))

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
  (layers/add! (get-layers tiled-map)
               (create-layer* tiled-map layer)))
