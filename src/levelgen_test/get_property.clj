(ns levelgen-test.get-property
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

; 2 function calls but should just be data
; => clojurize tiled-map before passing anywhere??
(defn f [tiled-map k]
  (-> tiled-map
      get-properties/f
      (map-properties/get k)))
