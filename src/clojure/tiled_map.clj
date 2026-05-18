(ns clojure.tiled-map)

(defprotocol TiledMap
  (properties [_])
  (layers [_]))
