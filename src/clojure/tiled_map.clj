(ns clojure.tiled-map)

(declare create)

(defprotocol TiledMap
  (dispose! [_])
  (properties [_])
  (layers [_]))
