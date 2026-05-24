(ns clojure.maps.tiled.tiles.static-tiled-map-tile)

(declare create)

(defprotocol Tile
  (copy [_])
  (properties [_]))
