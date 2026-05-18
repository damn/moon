(ns clojure.tiled-map)

(defprotocol TiledMap
  (properties [_])
  (layers [_])
  (add-layer! [_ {:keys [name visible?  properties tiles]}])
  )
