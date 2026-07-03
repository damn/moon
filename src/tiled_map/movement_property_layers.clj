(ns tiled-map.movement-property-layers
  (:require [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map-tile-layer.get-properties :as get-layer-properties]
            [clojure.gdx.tiled-map.get-layers :as get-layers]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers/f
       reverse
       (filter #(get/f (get-layer-properties/f %) "movement-properties"))))
