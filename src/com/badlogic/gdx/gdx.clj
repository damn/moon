(ns com.badlogic.gdx.gdx
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.math.vector3 :as vector3]
            [com.badlogic.gdx.scenes.scene2d.ui.image]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.widget-group]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.orthographic-camera :as camera]
            [gdl.graphics.shape-drawer :as shape-drawer]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.input :as input]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.ui.image :as image]
            [gdl.scene2d.ui.label :as label]
            [gdl.scene2d.ui.select-box :as select-box]
            [gdl.scene2d.ui.skin :as skin]
            [gdl.scene2d.ui.table]
            [gdl.scene2d.ui.text-field]
            [gdl.scene2d.ui.widget-group :as widget-group]
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
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               ImageButton
                                               Label
                                               HorizontalGroup
                                               SelectBox
                                               Skin
                                               Stack
                                               TooltipManager)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

; TODO actually coul;d 'bind-root' 'gdl.plattform/sprite-batch' or something
; for the constructors
; => just functions ...

(def state (atom nil))

(defn start!
  [{:keys [create
           dispose
           render
           resize
           title
           windowed-mode
           foreground-fps
           colors]}]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba)))
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (set! (.initialTime (TooltipManager/getInstance)) 0)
                          (reset! state
                                  (reduce (fn [ctx [f & params]]
                                            (apply f ctx params))
                                          {:ctx/app Gdx/app}
                                          create)))

                        (dispose [_]
                          (doseq [f dispose]
                            (f @state)))

                        (render [_]
                          (swap! state
                                 (fn [ctx]
                                   (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           ctx
                                           render))))

                        (resize [_ width height]
                          (doseq [f resize]
                            (f @state width height)))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))

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
     (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h))))

  (shape-drawer [batch texture-region]
    (ShapeDrawer. batch texture-region)))

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

(extend-type ShapeDrawer
  shape-drawer/ShapeDrawer
  (set-color! [this color-float-bits]
    (.setColor this (float color-float-bits)))

  (circle! [this x y radius]
    (.circle this x y radius))

  (ellipse! [this x y radius-x radius-y]
    (.ellipse this x y radius-x radius-y))

  (filled-circle! [this x y radius]
    (.filledCircle this (float x) (float y) (float radius)))

  (filled-rectangle! [this x y w h]
    (.filledRectangle this (float x) (float y) (float w) (float h)))

  (line! [this sx sy ex ey]
    (.line this (float sx) (float sy) (float ex) (float ey)))

  (rectangle! [this x y w h]
    (.rectangle this x y w h))

  (sector! [this center-x center-y radius start-radians radians]
    (.sector this center-x center-y radius start-radians radians))

  (default-line-width [this]
    (.getDefaultLineWidth this))

  (set-default-line-width! [this width]
    (.setDefaultLineWidth this width)))

(defmethod actor/create :ui/label
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type Label
  label/Label
  (set-text! [label text]
    (.setText label text)))

(defmethod actor/create :ui/select-box
  [{:keys [items selected skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array items))
    (.setSelected selected)))

(extend-type SelectBox
  select-box/SelectBox
  (selected [select-box]
    (.getSelected select-box)))

(defmethod actor/create :ui/stack
  [opts]
  (doto (Stack.)
    (group/set-opts! opts)))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (com.badlogic.gdx.scenes.scene2d.ui.image/create content)
    (actor/set-opts! opts)))

(extend-type Image
  image/Image
  (set-drawable! [image drawable]
    (.setDrawable image drawable)))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. (texture-region-drawable/create* drawable))
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Table
  gdl.scene2d.ui.table/Table
  (add! [table cell-declaration]
    (-> (table/add! table (:actor cell-declaration))
        (cell/set-opts! (dissoc cell-declaration :actor))))

  (add-rows! [table rows]
    (doseq [row rows]
      (doseq [props-or-actor row]

        (cond
         (map? props-or-actor)
         (gdl.scene2d.ui.table/add! table props-or-actor)

         ; TODO Remove else case
         :else (table/add! table props-or-actor)
         ))
      (table/row! table))
    table)

   (set-opts! [table opts]
     (when-let [rows (:table/rows opts)]
       (gdl.scene2d.ui.table/add-rows! table rows)
       (widget-group/pack! table))
     (when-let [defaults (:table/cell-defaults opts)]
       (cell/set-opts! (table/defaults table) defaults))
     (widget-group/set-opts! table opts)))

(defmethod actor/create :ui/table [opts]
  (doto (table/create)
    (gdl.scene2d.ui.table/set-opts! opts)))

(defmethod actor/create :ui/text-button
  [opts]
  (doto (text-button/create opts)
    (actor/set-opts! opts)))

(defmethod actor/create :ui/text-field
  [opts]
  (doto (text-field/create opts)
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.TextField
  gdl.scene2d.ui.text-field/TextField
  (text [text-field]
    (text-field/text text-field)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
  widget-group/WidgetGroup
  (pack! [widget-group]
    (com.badlogic.gdx.scenes.scene2d.ui.widget-group/pack! widget-group))

  (set-opts! [widget-group opts]
    (when (contains? opts :widget-group/fill-parent?)
      (com.badlogic.gdx.scenes.scene2d.ui.widget-group/set-fill-parent! widget-group (:widget-group/fill-parent? opts)))
    (group/set-opts! widget-group opts)))

(defn- window-set-opts! [window opts]
  (when (:window/modal? opts)
    (window/set-modal! window true))

  (when-let [skin (:window/close-button? opts)]
    (gdl.scene2d.ui.table/add! (window/title-table window)
                               {:actor (actor/create
                                        {:type :ui/text-button
                                         :text "X"
                                         :skin skin
                                         :actor/listeners {:listener/change (fn [_event _actor]
                                                                              (actor/remove! window))}})}))

  (gdl.scene2d.ui.table/set-opts! window opts))

(defmethod actor/create :ui/window
  [opts]
  (doto (window/create opts)
    (window-set-opts! opts)))
