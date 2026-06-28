(ns graphics.new-cursor
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics pixmap hotspot-x hotspot-y]
  (.newCursor ^Graphics graphics pixmap hotspot-x hotspot-y))
