(ns clojure.gdx.tiled-map.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f [tiled-map]
  (TiledMap/.getProperties tiled-map))
