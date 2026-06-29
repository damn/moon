(ns tiled-map.spawn-positions
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer$Cell)))

(defn f [^TiledMap tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (.get (.getLayers tiled-map) layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer x y)]
          :when cell
          :let [value (.get (.getProperties (.getTile cell))
                            property-key)]
          :when value]
      [position value])))
