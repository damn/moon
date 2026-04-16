(ns clojure.gdx.graphics
  (:require [clj.api.com.badlogic.gdx.graphics :as graphics]
            [clj.api.com.badlogic.gdx.graphics.pixmap :as pixmap]
            [clj.api.com.badlogic.gdx.graphics.pixmap.format :as pixmap.format]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable])
  (:import (com.badlogic.gdx.graphics GL20)))

(def frames-per-second graphics/frames-per-second)
(def delta-time graphics/delta-time)
(def set-cursor! graphics/set-cursor!)

(defn clear! [^com.badlogic.gdx.Graphics graphics r g b a]
  (.glClearColor (.getGL20 graphics) r g b a)
  (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

(defn new-cursor [graphics file-handle hotspot-x hotspot-y]
  (let [pixmap (pixmap/create file-handle)
        cursor (graphics/new-cursor graphics pixmap hotspot-x hotspot-y)]
    (disposable/dispose! pixmap)
    cursor))

(defn white-pixel-texture [_]
  (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (disposable/dispose! pixmap)
    texture))
