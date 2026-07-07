(ns clojure.load-schema-tiled-map
  (:require [clojure.load-tmx-map :as load-tmx-map]))

(defn f [w]
  (assoc w :schema-tiled-map (load-tmx-map/f "maps/modules.tmx")))
