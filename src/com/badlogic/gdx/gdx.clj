(ns com.badlogic.gdx.gdx
  (:require [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.input :as input])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          BitmapFont$BitmapFontData)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn start!
  [{:keys [listener config]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (let [{:keys [create!
                                    dispose!
                                    render!
                                    resize!
                                    pause!
                                    resume!]} (let [[f params] listener]
                                                (f params))]
                        (reify ApplicationListener
                          (create [_]
                            (create!))

                          (dispose [_]
                            (dispose!))

                          (render [_]
                            (render!))

                          (resize [_ width height]
                            (resize! width height))

                          (pause [_]
                            (pause!))

                          (resume [_]
                            (resume!))))
                      (let [{:keys [title
                                    windowed-mode
                                    foreground-fps]} config]
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps)))))

(defn app []
  Gdx/app)

(defn pixmap [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))

(defn texture [path]
  (Texture. ^String path))

(defn sprite-batch []
  (SpriteBatch.))

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
  (generate-font [generator {:keys [size]}]
    (.generateFont generator (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                               (set! (.size params) size)
                               ; Texture$TextureFilter/Linear because scaling to world-units
                               (set! (.minFilter params) Texture$TextureFilter/Linear)
                               (set! (.magFilter params) Texture$TextureFilter/Linear)
                               params)))

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

(extend-type Texture
  texture/Texture
  (region
    ([texture]
     (TextureRegion. texture))
    ([texture x y w h]
     (TextureRegion. texture (int x) (int y) (int w) (int h)))))

(extend-type TextureRegion
  texture-region/TextureRegion
  (width [this]
    (.getRegionWidth this))

  (height [this]
    (.getRegionHeight this)))

(extend-type SpriteBatch
  batch/Batch
  (begin! [batch]
    (.begin batch))

  (end! [batch]
    (.end batch))

  (set-color! [batch r g b a]
    (.setColor batch r g b a))

  (set-projection-matrix! [batch matrix]
    (.setProjectionMatrix batch matrix))

  (draw!
    ([batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
     (.draw batch
            ^TextureRegion texture-region
            x
            y
            origin-x
            origin-y
            width
            height
            scale-x
            scale-y
            rotation))
    ([batch texture-region x y w h]
     (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h)))))
