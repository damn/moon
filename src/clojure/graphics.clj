(ns clojure.graphics
  (:import (com.badlogic.gdx Graphics)))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn gl20 [^Graphics graphics]
  (.getGL20 graphics))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))

(defn new-cursor [^Graphics graphics pixmap hotspot-x hotspot-y]
  (.newCursor graphics pixmap hotspot-x hotspot-y))
