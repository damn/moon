(ns clojure.gdx.graphics
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn get-gl20 [graphics]
  (graphics/getGL20 graphics))

(defn get-frames-per-second [graphics]
  (graphics/getFramesPerSecond graphics))

(defn get-delta-time [graphics]
  (graphics/getDeltaTime graphics))

(defn create-cursor [graphics pixmap hotspot-x hotspot-y]
  (graphics/newCursor graphics pixmap hotspot-x hotspot-y))

(defn set-cursor! [graphics cursor]
  (graphics/setCursor graphics cursor))
