(ns graphics.new-cursor
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn f [graphics pixmap hotspot-x hotspot-y]
  (graphics/new-cursor graphics pixmap hotspot-x hotspot-y))
