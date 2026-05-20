(ns gdl.tiled-map)

(defprotocol TiledMap
  (dispose! [_])
  (properties [_])
  (layers [_])
  (add-layer! [_ {:keys [name visible?  properties tiles]}])
  (add-creatures-layer! [_ spawn-positions]))
