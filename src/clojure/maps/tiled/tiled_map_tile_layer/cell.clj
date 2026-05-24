(ns clojure.maps.tiled.tiled-map-tile-layer.cell)

(declare create)

(defprotocol Cell
  (tile [_]))
