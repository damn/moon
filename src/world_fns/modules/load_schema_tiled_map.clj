(ns world-fns.modules.load-schema-tiled-map
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn f [w]
  (assoc w :schema-tiled-map (.load (TmxMapLoader.) "maps/modules.tmx")))
