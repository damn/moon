(ns gdl.tiled-map)

(defprotocol TiledMap
  (dispose! [_])
  (properties [_])
  (layers [_]))
