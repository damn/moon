(ns com.badlogic.gdx.graphics
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Cursor Pixmap)))

(defn getDeltaTime [graphics]
  (.getDeltaTime ^Graphics graphics))

(defn getFramesPerSecond [graphics]
  (.getFramesPerSecond ^Graphics graphics))

(defn getGL20 [graphics]
  (.getGL20 ^Graphics graphics))

(defn newCursor [graphics ^Pixmap pixmap hotspot-x hotspot-y]
  (.newCursor ^Graphics graphics pixmap hotspot-x hotspot-y))

(defn setCursor [graphics ^Cursor cursor]
  (.setCursor ^Graphics graphics cursor))
