(ns world-fns.modules.load-schema-tiled-map
  (:require [clojure.gdx.load-tmx-map :as load-tmx-map]))

(defn f [w]
  (assoc w :schema-tiled-map (load-tmx-map/f "maps/modules.tmx")))
