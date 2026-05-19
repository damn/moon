(ns gdl.tiled-map.props
  (:refer-clojure :exclude [get]))

(defprotocol Props
  (get [_ k])
  (add! [_ m])
  (->clj [_]))
