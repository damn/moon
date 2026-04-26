(ns clojure.graphics
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics GL20
                                      Pixmap
                                      Pixmap$Format
                                      Texture)))


(defn frames-per-second [^Graphics graphics]
  (.getFramesPerSecond graphics))

(defn delta-time [^Graphics graphics]
  (.getDeltaTime graphics))

(defn set-cursor! [^Graphics graphics cursor]
  (.setCursor graphics cursor))

(defn clear! [^Graphics graphics r g b a]
  (.glClearColor (.getGL20 graphics) r g b a)
  (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

(defn new-cursor [^Graphics graphics file-handle hotspot-x hotspot-y]
  (let [pixmap (Pixmap. ^FileHandle file-handle)
        cursor (.newCursor graphics pixmap hotspot-x hotspot-y)]
    (.dispose pixmap)
    cursor))

(defn white-pixel-texture [^Graphics _graphics]
  (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                 (.setColor 1 1 1 1)
                 (.drawPixel 0 0))
        texture (Texture. pixmap)]
    (.dispose pixmap)
    texture))
