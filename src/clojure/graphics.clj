(ns clojure.graphics
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Cursor Pixmap)))

(defn get-delta-time [graphics]
  (Graphics/.getDeltaTime graphics))

(defn get-frames-per-second [graphics]
  (Graphics/.getFramesPerSecond graphics))

(defn get-gl20 [graphics]
  (Graphics/.getGL20 graphics))

(defn new-cursor [graphics ^Pixmap pixmap hotspot-x hotspot-y]
  (Graphics/.newCursor graphics pixmap hotspot-x hotspot-y))

(defn set-cursor! [graphics ^Cursor cursor]
  (Graphics/.setCursor graphics cursor))
