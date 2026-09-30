(ns gdx.graphics
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Cursor
                                      Pixmap)))

(defn get-gl20 [graphics]
  (.getGL20 ^Graphics graphics))

(defn get-frames-per-second [graphics]
  (.getFramesPerSecond ^Graphics graphics))

(defn get-delta-time [graphics]
  (.getDeltaTime ^Graphics graphics))

(defn create-cursor [graphics pixmap hotspot-x hotspot-y]
  (.newCursor ^Graphics graphics ^Pixmap pixmap hotspot-x hotspot-y))

(defn set-cursor! [graphics cursor]
  (.setCursor ^Graphics graphics ^Cursor cursor))
