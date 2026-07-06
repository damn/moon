(ns tiled-map.movement-property-layers
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer.get-properties :as get-layer-properties]
            [clojure.gdx.tiled-map.get-layers :as get-layers]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers/f
       reverse
       (filter #(map-properties/get (get-layer-properties/f %) "movement-properties"))))
