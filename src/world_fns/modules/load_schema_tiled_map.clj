(ns world-fns.modules.load-schema-tiled-map
  (:require [clojure.gdx :as gdx]))

(defn f [w]
  (assoc w :schema-tiled-map (gdx/tmx-map-loader-load "maps/modules.tmx")))
