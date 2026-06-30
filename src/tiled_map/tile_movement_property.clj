(ns tiled-map.tile-movement-property
  (:require [clojure.gdx :as gdx]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (gdx/tiled-map-tile-layer-get-cell layer x y)]
      (let [value (gdx/map-properties-get (gdx/tile-get-properties (gdx/cell-get-tile cell))
                                          "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (gdx/tiled-map-tile-layer-get-name layer) " is undefined."))
        value))))
