(ns moon.modules.load-schema-tiled-map
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn step [w]
  (assoc w :schema-tiled-map (.load (TmxMapLoader.) "maps/modules.tmx")))
