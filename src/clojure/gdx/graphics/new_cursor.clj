(ns clojure.gdx.graphics.new-cursor
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Pixmap)))

(defn f [graphics ^Pixmap pixmap hotspot-x hotspot-y]
  (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y))
