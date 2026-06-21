(ns clojure.tiled-map.add-creatures-layer
  (:require [clojure.tiled-map.add-layer :as add-layer]
            [clojure.create-static-tiled-map-tile :as create-tile]))

; out of memory error -> each texture region is a new object
; so either memoize on id or property/image already calculated !? idk
(def ^:private creature-tile
  (memoize
   (fn [{:keys [tile/id
                tile/texture-region]}]
     (assert (and id
                  texture-region))
     (create-tile/f texture-region "id" id))))

(defn f [tiled-map spawn-positions]
  (add-layer/f tiled-map
               {:name "creatures"
                :visible? false
                :tiles (for [[position creature-property] spawn-positions]
                         [position (creature-tile creature-property)])}))
