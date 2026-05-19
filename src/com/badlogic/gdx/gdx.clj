(ns com.badlogic.gdx.gdx
  (:require [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]
            [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.input :as input])
  (:import (com.badlogic.gdx Application
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          BitmapFont$BitmapFontData)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn app []
  Gdx/app)

(defn pixmap [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))

(extend-type Application
  app/App
  (audio [app]
    (.getAudio app))

  (files [app]
    (.getFiles app))

  (graphics [app]
    (.getGraphics app))

  (input [app]
    (.getInput app)))

(extend-type Audio
  audio/Audio
  (new-sound [audio file-handle]
    (.newSound audio file-handle)))

(extend-type Files
  files/Files
  (internal [files path]
    (.internal files path)))

(extend-type Graphics
  graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (new-cursor [graphics pixmap hotspot-x hotspot-y]
    (.newCursor graphics pixmap hotspot-x hotspot-y))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (gl20 [graphics]
    (.getGL20 graphics)))

(extend-type Input
  input/Input
  (set-processor! [this input-processor]
    (.setInputProcessor this input-processor))

  (key-pressed? [this key]
    (.isKeyPressed this key))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this key))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this button))

  (mouse-position [this]
    [(.getX this)
     (.getY this)]))

(extend-type FileHandle
  file-handle/FileHandle
  (list [file]
    (.list file))

  ( directory? [file]
    (.isDirectory file))

  (extension [this]
    (.extension this))

  (path [this]
    (.path this))

  (freetype-font-generator [this]
    (FreeTypeFontGenerator. this))

  (pixmap [this]
    (Pixmap. this)))

(extend-type FreeTypeFontGenerator
  font-generator/FreeTypeFontGenerator
  (generate-font [generator parameter]
    (.generateFont generator (parameter/create parameter)))

  (dispose! [generator]
    (.dispose generator)))

(extend-type BitmapFont
  bitmap-font/BitmapFont
  (data [font]
    (.getData font))

  (line-height [font]
    (.getLineHeight font))

  (draw! [font batch text x y target-width align wrap?]
    (.draw font
           batch
           text
           (float x)
           (float y)
           (float target-width)
           align
           wrap?))

  (use-integer-positions! [font bool]
    (.setUseIntegerPositions font bool)))

(extend-type BitmapFont$BitmapFontData
  bitmap-font.data/Data
  (scale-x [data]
    (.scaleX data))

  (set-scale! [data scale]
    (.setScale data scale))

  (set-markup-enabled! [data enabled?]
    (set! (.markupEnabled data) enabled?)))

(extend-type Pixmap
  pixmap/Pixmap
  (texture [pixmap]
    (Texture. pixmap))

  (set-color! [pixmap r g b a]
    (.setColor pixmap r g b a))

  (draw-pixel! [pixmap x y]
    (.drawPixel pixmap x y))

  (dispose! [pixmap]
    (.dispose pixmap)))
