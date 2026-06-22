(ns gdl.graphics.new-cursor
  (:import (com.badlogic.gdx Graphics)))

(defn f [^Graphics graphics pixmap hotspot-x hotspot-y]
  (.newCursor graphics pixmap hotspot-x hotspot-y))
