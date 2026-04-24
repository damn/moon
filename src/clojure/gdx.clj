(ns clojure.gdx
  (:require clojure.audio
            clojure.audio.sound
            clojure.files
            clojure.files.file-handle
            [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.g2d.bitmap-font.data :as data]
            [clojure.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clojure.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clojure.gdx.graphics.pixmap :as pixmap]
            [clojure.gdx.graphics.pixmap.format :as pixmap.format]
            [clojure.gdx.graphics.texture.filter :as texture.filter]
            [clojure.gdx.input.buttons :as buttons]
            [clojure.gdx.input.keys :as keys]
            [clojure.gdx.utils.align :as align]
            [clojure.gdx.utils.disposable :as disposable]
            clojure.graphics
            clojure.graphics.bitmap-font
            clojure.graphics.color
            clojure.graphics.freetype
            clojure.graphics.texture-region
            clojure.input
            [clojure.string :as str])
  (:import (clojure.lang PersistentVector)
           (com.badlogic.gdx Application
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Color
                                      GL20)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          TextureRegion)))

(defn app []
  Gdx/app)

(defn audio []
  Gdx/audio)

(defn graphics []
  Gdx/graphics)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)

(extend-type Files
  clojure.files/Files
  (internal [this path]
    (.internal this path)))

(extend-type FileHandle
  clojure.files.file-handle/FileHandle
  (list [this]
    (.list this))

  (directory? [this]
    (.isDirectory this))

  (extension [this]
    (.extension this))

  (path [this]
    (.path this)))

(extend-type Input
  clojure.input/Input
  (key-pressed? [this key]
    (.isKeyPressed this (keys/k->value key)))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this (keys/k->value key)))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this (buttons/k->value button)))

  (mouse-position [this]
    [(.getX this)
     (.getY this)])

  (set-processor! [this input-processor]
    (.setInputProcessor this input-processor)))

(extend-type Audio
  clojure.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  clojure.audio.sound/Sound
  (play! [this]
    (.play this)))

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
      (disposable/dispose! pixmap)
      cursor))

  (white-pixel-texture [_graphics]
    (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                   (pixmap/set-color! 1 1 1 1)
                   (pixmap/draw-pixel! 0 0))
          texture (pixmap/texture pixmap)]
      (disposable/dispose! pixmap)
      texture)))

(extend-type Application
  clojure.graphics.freetype/Freetype
  (generate-font [_application file-handle {:keys [size]}]
    (let [generator (font-generator/create file-handle)
          font (font-generator/generate-font generator
                                             (doto (parameter/create)
                                               (parameter/set-size! size)
                                               (parameter/set-min-filter! texture.filter/linear)
                                               (parameter/set-mag-filter! texture.filter/linear)))]
      (disposable/dispose! generator)
      font)))

(extend-type BitmapFont
  clojure.graphics.bitmap-font/BitmapFont
  (scale-x [font]
    (data/scale-x (font/data font)))

  (set-scale! [font scale]
    (data/set-scale! (font/data font) scale))

  (enable-markup! [font enable?]
    (data/enable-markup! (font/data font) enable?))

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
