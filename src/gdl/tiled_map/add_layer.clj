(ns gdl.tiled-map.add-layer
  (:require [gdl.add :refer [add!]]
            [gdl.get :refer [props-get]]
            [gdl.tiled-map.get-layers :refer [get-layers]]
            [gdl.get-properties :refer [get-properties]]
            [gdl.tiled-map-tile-layer.create :as create-layer]))

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
