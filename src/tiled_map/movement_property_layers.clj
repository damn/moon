(ns tiled-map.movement-property-layers
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [^TiledMap tiled-map]
  (->> tiled-map
       .getLayers
       reverse
       (filter #(.get (.getProperties %) "movement-properties"))))
