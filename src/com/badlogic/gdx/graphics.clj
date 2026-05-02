(ns com.badlogic.gdx.graphics
  (:import (com.badlogic.gdx Graphics)))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))

(defn gl20 [^Graphics graphics]
  (.getGL20 graphics))
