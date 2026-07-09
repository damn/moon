(ns clojure.gdx.graphics
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn get-delta-time [& args]
  (apply graphics/get-delta-time args))

(defn get-frames-per-second [& args]
  (apply graphics/get-frames-per-second args))

(defn get-gl20 [& args]
  (apply graphics/get-gl20 args))

(defn new-cursor [& args]
  (apply graphics/new-cursor args))

(defn set-cursor! [& args]
  (apply graphics/set-cursor! args))
