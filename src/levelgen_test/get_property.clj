(ns levelgen-test.get-property
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]))

; 2 function calls but should just be data
; => clojurize tiled-map before passing anywhere??
(defn f [tiled-map k]
  (-> tiled-map
      tiled-map/get-properties
      (map-properties/get k)))
