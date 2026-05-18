(ns clojure.tiled-map.layer
  (:refer-clojure :exclude [name]))

(defprotocol Layer
  (properties [_])
  (name [_])
  (cell [_ [x y]])
  (set-cell! [_ [x y] cell])
  (width [_])
  (height [_])
  (visible? [_])
  (property-value [_ pos property-key]))
