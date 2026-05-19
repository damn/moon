(ns gdl.tiled-map.tile)

(defprotocol Tile
  (copy [_])
  (properties [_]))
