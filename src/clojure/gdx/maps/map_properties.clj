(ns clojure.gdx.maps.map-properties
  (:refer-clojure :exclude [get]))

(defprotocol Props
  (get [_ k])
  (add! [_ m])
  (->clj [_]))


