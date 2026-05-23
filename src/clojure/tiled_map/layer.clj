(ns clojure.tiled-map.layer
  (:refer-clojure :exclude [name]))

(defn create
  [{:keys [width
           height
           tilewidth
           tileheight
           name
           visible?
           map-properties
           tiles]}])

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
