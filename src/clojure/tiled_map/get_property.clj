(ns clojure.tiled-map.get-property
  (:require [gdl.tiled-map :as tiled-map]
            [gdl.map-properties :as map-properties]))

; 2 function calls but should just be data
; => clojurize tiled-map before passing anywhere??
(defn f [tiled-map k]
  (-> tiled-map
      tiled-map/get-properties
      (map-properties/get k)))
