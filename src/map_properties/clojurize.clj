(ns map-properties.clojurize
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn f [props]
  (zipmap (map-properties/keys props)
          (map-properties/values props)))
