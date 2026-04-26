(ns clojure.gdx.graphics
  (:require clojure.graphics
            clojure.graphics.color
            clojure.graphics.texture-region)
  (:import (clojure.lang PersistentVector)
           (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color
                                      GL20
                                      Pixmap
                                      Pixmap$Format
                                      Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(extend-type Graphics
  clojure.graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (clear! [graphics r g b a]
    (.glClearColor (.getGL20 graphics) r g b a)
    (.glClear      (.getGL20 graphics) GL20/GL_COLOR_BUFFER_BIT))

  (new-cursor [graphics file-handle hotspot-x hotspot-y]
    (let [pixmap (Pixmap. ^FileHandle file-handle)
          cursor (.newCursor graphics pixmap hotspot-x hotspot-y)]
      (.dispose pixmap)
      cursor))

  (white-pixel-texture [_graphics]
    (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                   (.setColor 1 1 1 1)
                   (.drawPixel 0 0))
          texture (Texture. pixmap)]
      (.dispose pixmap)
      texture)))

(extend-type PersistentVector
  clojure.graphics.color/Color
  (float-bits [[r g b a]]
    (Color/toFloatBits (float r)
                       (float g)
                       (float b)
                       (float a))))

(extend-type TextureRegion
  clojure.graphics.texture-region/TextureRegion
  (width [texture-region]
    (.getRegionWidth texture-region))

  (height [texture-region]
    (.getRegionHeight texture-region)))
