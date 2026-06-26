(ns tiled-map-tile-layer.set-visible
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn set-visible! [layer bool]
  (layer/set-visible! layer bool))
