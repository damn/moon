(ns clojure.gdx.graphics
  (:require [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.pixmap :as pixmap]
            [clojure.gdx.graphics.pixmap.format :as pixmap.format]
            [clojure.gdx.utils.align :as align]
            clojure.graphics
            clojure.graphics.bitmap-font
            clojure.graphics.color
            clojure.graphics.texture-region
            [clojure.string :as str])
  (:import (clojure.lang PersistentVector)
           (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Color
                                      GL20)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          TextureRegion)))

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
    (let [pixmap (pixmap/create file-handle)
          cursor (.newCursor graphics pixmap hotspot-x hotspot-y)]
      (.dispose pixmap)
      cursor))

  (white-pixel-texture [_graphics]
    (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                   (pixmap/set-color! 1 1 1 1)
                   (pixmap/draw-pixel! 0 0))
          texture (pixmap/texture pixmap)]
      (.dispose pixmap)
      texture)))

(extend-type BitmapFont
  clojure.graphics.bitmap-font/BitmapFont
  (scale-x [font]
    (.scaleX (font/data font)))

  (set-scale! [font scale]
    (.setScale (font/data font) scale))

  (enable-markup! [font enable?]
    (set! (.markupEnabled (font/data font)) enable?))

  (use-integer-positions! [font use-integer-positions?]
    (font/use-integer-positions! font use-integer-positions?))

  (draw! [font batch text x y target-width h-align wrap?]
    (font/draw! font
                batch
                text
                x
                y
                target-width
                (align/k->value h-align)
                wrap?))

  (text-height [font text]
    (-> text
        (str/split #"\n")
        count
        (* (font/line-height font)))))


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
