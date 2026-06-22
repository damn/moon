(ns gdl.tiled-map.tile-movement-property
  (:require [gdl.get :refer [props-get]]
            [gdl.get-properties :refer [get-properties]]
            [gdl.tiled-map-tile-layer.get-name :refer [get-name]]
            [gdl.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.tiled-map-tile-layer.cell :as cell]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer position)]
      (let [value (-> cell
                      cell/tile
                      get-properties
                      (props-get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props-get (get-properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))
