(ns map-properties.get
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn f [props k]
  (map-properties/get props k))
