(ns clojure.maps.map-layers
  (:refer-clojure :exclude [get]))

(defprotocol Layers
  (add! [_ layer])
  (get [_ layer-name])
  (get-index [_ layer]))
