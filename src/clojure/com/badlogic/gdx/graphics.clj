(ns clojure.com.badlogic.gdx.graphics
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Cursor Pixmap)))

(defn new-cursor [^Graphics graphics ^Pixmap pixmap hotspot-x hotspot-y]
  (.newCursor graphics pixmap hotspot-x hotspot-y))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))
