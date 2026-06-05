(ns clojure.add
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(defprotocol Add
  (add! [_ item]))

(extend-type MapLayers
  Add
  (add! [layers ^MapLayer layer]
    (.add layers layer)))
