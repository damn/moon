(ns clojure.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name]))

(declare create)

(defprotocol Layer
  (set-visible! [_ visible?])
  (set-name! [_ name])
  (properties [_])
  (name [_])
  (cell [_ [x y]])
  (set-cell! [_ [x y] cell])
  (width [_])
  (height [_])
  (visible? [_]))
