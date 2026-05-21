(ns gdl.tiled-map.tile)

(declare create)

(defprotocol Tile
  (copy [_])
  (properties [_]))
