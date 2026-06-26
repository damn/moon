(ns map-properties.put
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn f [props k v]
  (map-properties/put! props k v))
