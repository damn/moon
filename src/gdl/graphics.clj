(ns gdl.graphics
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn get-delta-time [& args]
  (apply graphics/getDeltaTime args))

(defn get-frames-per-second [& args]
  (apply graphics/getFramesPerSecond args))

(defn get-gl20 [& args]
  (apply graphics/getGL20 args))

(defn new-cursor [& args]
  (apply graphics/newCursor args))

(defn set-cursor! [& args]
  (apply graphics/setCursor args))
