(ns clojure.gdx.tiled-map.get-layers
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f [tiled-map]
  (TiledMap/.getLayers tiled-map))
