(ns clojure.tiled-map.get-property
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]))

; 2 function calls but should just be data
; => clojurize tiled-map before passing anywhere??
(defn f [tiled-map k]
  (-> tiled-map
      tiled-map/getProperties
      (map-properties/get k)))
