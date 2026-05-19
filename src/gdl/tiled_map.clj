(ns gdl.tiled-map)

(defprotocol TiledMap
  (properties [_])
  (layers [_])
  (add-layer! [_ {:keys [name visible?  properties tiles]}])
  (movement-property [_ position])
  (spawn-positions [_])
  (add-creatures-layer! [_ spawn-positions]))
