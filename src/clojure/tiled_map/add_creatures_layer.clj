(ns clojure.tiled-map.add-creatures-layer
  (:require [clojure.tiled-map.add-layer :as add-layer]
            [clojure.map-properties :as map-properties]
            [clojure.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f [tiled-map spawn-positions]
  (add-layer/f tiled-map
               (let [creature-tile (memoize
                                    (fn [{:keys [tile/id
                                                 tile/texture-region]}]
                                      (assert (and id
                                                   texture-region))
                                      (let [tile (static-tiled-map-tile/new texture-region)]
                                        (map-properties/put! (static-tiled-map-tile/get-properties tile) "id" id)
                                        tile)))]
                 {:name "creatures"
                  :visible? false
                  :tiles (for [[position creature-property] spawn-positions]
                           [position (creature-tile creature-property)])})))
