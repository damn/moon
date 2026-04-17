(ns clojure.gdx.graphics
  (:require [clojure.gdx.graphics.pixmap :as pixmap]
            [clojure.gdx.graphics.pixmap.format :as pixmap.format]
            [clojure.gdx.utils.disposable :as disposable])
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics GL20)))

(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))

(defn clear! [^com.badlogic.gdx.Graphics graphics r g b a]
  (.glClearColor (.getGL20 graphics) r g b a)
  (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

(defn new-cursor [^Graphics graphics file-handle hotspot-x hotspot-y]
  (let [pixmap (pixmap/create file-handle)
        cursor (.newCursor graphics pixmap hotspot-x hotspot-y)]
    (disposable/dispose! pixmap)
    cursor))

(defn white-pixel-texture [_]
  (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (disposable/dispose! pixmap)
    texture))
