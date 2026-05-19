(ns com.badlogic.gdx.gdx
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.math.vector3 :as vector3]
            [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.orthographic-camera :as camera]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.input :as input]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.ui.skin :as skin]
            [gdl.utils.disposable :as disposable]
            [gdl.utils.viewport :as viewport])
  (:import (clojure.lang ILookup)
           (com.badlogic.gdx Application
                             ApplicationListener
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter
                                      OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          BitmapFont$BitmapFontData)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup
                                               Skin
                                               TooltipManager)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)))

; TODO actually coul;d 'bind-root' 'gdl.plattform/sprite-batch' or something
; for the constructors
; => just functions ...

(defn start!
  [{:keys [listener config colors]}]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba)))
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
                            (set! (.initialTime (TooltipManager/getInstance)) 0)
                            (create! Gdx/app))

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

(defn pixmap [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))

(defn texture [path]
  (Texture. ^String path))

(defn sprite-batch []
  (SpriteBatch.))

(defn fit-viewport
  ([width height]
   (proxy [FitViewport ILookup] [width height]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this)))))
  ([width height camera]
   (proxy [FitViewport ILookup] [width height camera]
     (valAt [k]
       (case k
         :viewport/camera       (FitViewport/.getCamera      this)
         :viewport/world-width  (FitViewport/.getWorldWidth  this)
         :viewport/world-height (FitViewport/.getWorldHeight this))))))

(defn orthographic-camera [{:keys [y-down? world-width world-height]}]
  (doto (OrthographicCamera.)
    (.setToOrtho y-down? world-width world-height)))

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
    (Pixmap. this))

  (skin [this]
    (Skin. this)))

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

(extend-type Disposable
  disposable/Disposable
  (dispose! [this]
    (.dispose this)))

(extend-type Event
  event/Event
  (stage [event]
    (.getStage event)))

(extend-type FitViewport
  viewport/Viewport
  (update! [viewport screen-width screen-height center-camera?]
    (.update viewport screen-width screen-height center-camera?))

  (unproject [viewport position]
    (-> viewport
        (.unproject (vector2/->java position))
        vector2/->clj)))

(extend-type OrthographicCamera
  gdl.graphics.orthographic-camera/OrthographicCamera
  (combined [camera]
    (.combined camera))

  (zoom [camera]
    (.zoom camera))

  (frustum [camera]
    (let [plane-points (mapv vector3/->clj (.planePoints (.frustum camera)))
          frustum-points (take 4 plane-points)
          left-x   (apply min (map first  frustum-points))
          right-x  (apply max (map first  frustum-points))
          bottom-y (apply min (map second frustum-points))
          top-y    (apply max (map second frustum-points))]
      [left-x right-x bottom-y top-y]))

  (position [camera]
    (vector3/->clj (.position camera)))

  (set-position! [camera [x y]]
    (set! (.x (.position camera)) x)
    (set! (.y (.position camera)) y)
    (.update camera))

  (set-zoom! [camera amount]
    (set! (.zoom camera) amount)
    (.update camera))

  (inc-zoom! [cam by]
    (camera/set-zoom! cam (max 0.1 (+ (camera/zoom cam) by))))

  (visible-tiles [camera]
    (let [[left-x right-x bottom-y top-y] (camera/frustum camera)]
      (for [x (range (int left-x)   (int right-x))
            y (range (int bottom-y) (+ 2 (int top-y)))]
        [x y])))

  (calculate-zoom [camera {:keys [left top right bottom]}]
    (let [viewport-width  (.viewportWidth  camera)
          viewport-height (.viewportHeight camera)
          [px py] (camera/position camera)
          px (float px)
          py (float py)
          leftx (float (left 0))
          rightx (float (right 0))
          x-diff (max (- px leftx) (- rightx px))
          topy (float (top 1))
          bottomy (float (bottom 1))
          y-diff (max (- topy py) (- py bottomy))
          vp-ratio-w (/ (* x-diff 2) viewport-width)
          vp-ratio-h (/ (* y-diff 2) viewport-height)
          new-zoom (max vp-ratio-w vp-ratio-h)]
      new-zoom)))

(extend-type Skin
  skin/Skin
  (font [skin name]
    (.getFont skin name)))

(defmethod actor/create :ui/horizontal-group
  [{:keys [space pad] :as opts}]
  (doto (HorizontalGroup.)
    (.space space)
    (.pad pad)
    (actor/set-opts! opts)))
