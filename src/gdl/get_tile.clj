(ns gdl.get-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn f [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
