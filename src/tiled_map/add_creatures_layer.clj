(ns tiled-map.add-creatures-layer
  (:require [tiled-map.add-layer :as add-layer]
            [tiled.create-static-tiled-map-tile :as create-tile]))

(defn f [tiled-map spawn-positions]
  (add-layer/f tiled-map
               (let [creature-tile (memoize
                                    (fn [{:keys [tile/id
                                                 tile/texture-region]}]
                                      (assert (and id
                                                   texture-region))
                                      (create-tile/f texture-region "id" id)))]
                 {:name "creatures"
                  :visible? false
                  :tiles (for [[position creature-property] spawn-positions]
                           [position (creature-tile creature-property)])})))
