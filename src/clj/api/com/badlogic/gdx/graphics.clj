(ns clj.api.com.badlogic.gdx.graphics
  (:import (com.badlogic.gdx Graphics)))

(defn new-cursor [^Graphics graphics pixmap hotspot-x hotspot-y]
  (.newCursor graphics pixmap hotspot-x hotspot-y))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))
