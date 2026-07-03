(ns levelgen-test.get-property
  (:require [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

; 2 function calls but should just be data
; => clojurize tiled-map before passing anywhere??
(defn f [tiled-map k]
  (-> tiled-map
      get-properties/f
      (get/f k)))
