(ns com.badlogic.gdx.application
  (:require [clojure.config :refer [edn-resource]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx.textures]
            com.badlogic.gdx.maps.renderer
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.image]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.widget-group]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.utils.align :as align]
            [gdl.app :as app]
            [gdl.sound :as sound]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.shape-drawer :as shape-drawer]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.input :as input]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.stage :as stage]
            [gdl.scene2d.ui.check-box :as check-box]
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
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          BitmapFont$BitmapFontData)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            CtxStage
                                            Event
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox
                                               Image
                                               ImageButton
                                               Label
                                               HorizontalGroup
                                               ScrollPane
                                               SelectBox
                                               Skin
                                               Stack
                                               Widget)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport FitViewport)
           (space.earlygrey.shapedrawer ShapeDrawer))
  (:gen-class))

(extend-type Application
  app/App
  (graphics [app]
    (.getGraphics app))

  (input [app]
    (.getInput app)))

(extend-type Graphics
  graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (gl20 [graphics]
    (.getGL20 graphics)))

(extend-type Input
  input/Input
  (key-pressed? [this key]
    (.isKeyPressed this key))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this key))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this button))

  (mouse-position [this]
    [(.getX this)
     (.getY this)]))

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
           (align/k->value align)
           wrap?)))

(extend-type BitmapFont$BitmapFontData
  font.data/Data
  (scale-x [data]
    (.scaleX data))

  (set-scale! [data scale]
    (.setScale data scale))

  (set-markup-enabled! [data enabled?]
    (set! (.markupEnabled data) enabled?)))

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
  (draw-tiled-map! [batch world-unit-scale camera tiled-map color-setter]
    (com.badlogic.gdx.maps.renderer/draw! batch
                                          world-unit-scale
                                          camera
                                          tiled-map
                                          color-setter))

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
  (doto (com.badlogic.gdx.scenes.scene2d.ui.image/create (if (map? content)
                                                           (texture-region-drawable/create content)
                                                           content))
    (actor/set-opts! opts)))

(extend-type Image
  image/Image
  (set-drawable! [image drawable]
    (.setDrawable image (texture-region-drawable/create drawable))))

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

(defmethod actor/create :ui/actor
  [{:keys [act! draw!] :as opts}]
  (doto (proxy [Actor] []
          (act [delta]
            (when act!
              (act! this delta))
            (let [^Actor this this]
              (proxy-super act delta)))
          (draw [batch parent-alpha]
            (when draw!
              (draw! this batch parent-alpha))))
    (actor/set-opts! opts)))

(extend-type Actor
  actor/Actor
  (name [actor]
    (.getName actor))

  (x [actor]
    (.getX actor))

  (y [actor]
    (.getY actor))

  (width [actor]
    (.getWidth actor))

  (height [actor]
    (.getHeight actor))

  (user-object [actor]
    (.getUserObject actor))

  (stage [actor]
    (.getStage actor))

  (set-name! [actor name]
    (.setName actor name))

  (set-user-object! [actor object]
    (.setUserObject actor object))

  (visible? [actor]
    (.isVisible actor))

  (hit [^Actor actor [x y] touchable?]
    (.hit actor x y touchable?))

  (remove! [actor]
    (.remove actor))

  (parent [actor]
    (.getParent actor))

  (set-position!
    ([actor [x y]]
     (.setPosition actor x y))
    ([actor x y align]
     (.setPosition actor x y (align/k->value align))))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (set-touchable! [actor touchable]
    (.setTouchable actor (touchable/k->value touchable)))

  (add-listener! [actor [listener-k listener-params]]
    (.addListener actor
                  (case listener-k
                    :listener/change (change-listener/create listener-params)
                    :listener/text-tooltip (text-tooltip/create listener-params)
                    :listener/click (click-listener/create listener-params))))

  (stage->local-coordinates [actor xy]
    (vector2/->clj (.stageToLocalCoordinates actor (vector2/->java xy))))

  (find-ancestor [actor pred]
    (if-let [p (actor/parent actor)]
      (if (pred p)
        p
        (actor/find-ancestor p pred))
      (throw (Error. (str "Actor has no parent window " actor)))))

  (toggle-visible! [actor]
    (actor/set-visible! actor (not (actor/visible? actor))))

  (set-opts! [actor opts]
    (when-let [user-object (:actor/user-object opts)]
      (actor/set-user-object! actor user-object))

    (when (:actor/position opts)
      (let [[x y align] (:actor/position opts)]
        (if align
          (actor/set-position! actor x y align)
          (actor/set-position! actor [x y]))))

    (when (contains? opts :actor/visible?)
      (actor/set-visible! actor (:actor/visible? opts)))

    (when-let [touchable (:actor/touchable opts)]
      (actor/set-touchable! actor touchable))

    (when-let [name (:actor/name opts)]
      (actor/set-name! actor name))

    (when-let [listeners (:actor/listeners opts)]
      (doseq [listener listeners]
        (actor/add-listener! actor listener)))))

(defmethod actor/create :ui/group
  [opts]
  (doto (Group.)
    (group/set-opts! opts)))

(extend-type Group
  group/Group
  (add-actor! [group actor]
    (.addActor group actor))

  (children [group]
    (.getChildren group))

  (find-actor [group name]
    (.findActor group name))

  (clear-children! [group]
    (.clearChildren group))

  (set-opts! [group opts]
    (when-let [actors (:group/actors opts)]
      (run! #(group/add-actor! group (actor/create %)) actors))
    (actor/set-opts! group opts)))

(defmethod actor/create :ui/scroll-pane
  [{:keys [^Actor actor ^Skin skin]}]
  (ScrollPane. actor skin))

(defmethod actor/create :ui/widget
  [{:keys [draw!]}]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))

(extend-type CtxStage
  stage/Stage
  (set-ctx! [stage ctx]
    (set! (.ctx stage) ctx))

  (add-actor! [stage actor]
    (.addActor stage (actor/create actor)))

  (act! [stage]
    (.act stage))

  (draw! [stage]
    (.draw stage))

  (find-actor [stage name]
    (-> stage
        .getRoot
        (group/find-actor name)))

  (mouseover-actor [stage position]
    (let [[x y] (-> stage :stage/viewport (viewport/unproject position))]
      (.hit stage x y true))))

(defmethod actor/create :ui/check-box
  [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(extend-type CheckBox
  check-box/CheckBox
  (checked? [this]
    (.isChecked this)))

(extend-type Sound
  sound/Sound
  (play! [this]
    (.play this)))

(defn- create-stage [viewport batch]
  (proxy [CtxStage ILookup] [viewport batch]
    (valAt [k]
      (case k
        ; TODO :stage/root
        :stage/ctx      (.ctx         ^CtxStage this)
        :stage/viewport (.getViewport ^CtxStage this)))))

(defn- fit-viewport
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

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (let [{:keys [create
                dispose
                render
                resize
                title
                windowed-mode
                foreground-fps
                colors
                ]} (edn-resource "start.edn")]
    (Lwjgl3Application. (reify ApplicationListener
                          (create [_]
                            (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
                            (tooltip-manager/set-initial-time 0)
                            (reset! state
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            (let [batch (SpriteBatch.)
                                                  white-pixel-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                                                                     (.setColor 1 1 1 1)
                                                                                     (.drawPixel 0 0))
                                                                            texture (Texture. pixmap)]
                                                                        (.dispose pixmap)
                                                                        texture)
                                                  world-unit-scale (float (/ 48))]
                                              {:ctx/app Gdx/app
                                               :ctx/audio (into {}
                                                                (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                                                                  [sound-name
                                                                   (.newSound Gdx/audio
                                                                              (.internal Gdx/files (format "sounds/%s.wav" sound-name)))]))
                                               :ctx/batch batch
                                               :ctx/shape-drawer-texture white-pixel-texture
                                               :ctx/shape-drawer (ShapeDrawer. batch (texture/region white-pixel-texture 1 0 1 1))
                                               :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                                                                       size 16
                                                                       quality-scaling 2
                                                                       generator (FreeTypeFontGenerator. (.internal Gdx/files path))
                                                                       font (.generateFont generator (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                                                                       (set! (.size params) (* size quality-scaling))
                                                                                                       ; Texture$TextureFilter/Linear because scaling to world-units
                                                                                                       (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                                                                       (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                                                                       params))]
                                                                   (.dispose generator)
                                                                   (font.data/set-scale! (.getData font) (/ quality-scaling))
                                                                   (font.data/set-markup-enabled! (.getData font) true)
                                                                   (.setUseIntegerPositions font false)
                                                                   font)
                                               :ctx/world-unit-scale world-unit-scale
                                               :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                                                                         world-height (* 900  world-unit-scale)]
                                                                     (fit-viewport world-width
                                                                                   world-height
                                                                                   (orthographic-camera/create
                                                                                    {:y-down? false
                                                                                     :world-width world-width
                                                                                     :world-height world-height})))
                                               :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                                                              (update-vals data
                                                                           (fn [[path [hotspot-x hotspot-y]]]
                                                                             (let [pixmap (Pixmap. (.internal Gdx/files (format path-format path)))
                                                                                   cursor (.newCursor Gdx/graphics pixmap hotspot-x hotspot-y)]
                                                                               (.dispose pixmap)
                                                                               cursor))))
                                               :ctx/stage (let [stage (create-stage (fit-viewport 1440 900) batch)]
                                                            (.setInputProcessor Gdx/input stage)
                                                            stage)
                                               :ctx/skin (let [skin (Skin. (.internal Gdx/files "uiskin.json"))]
                                                           (-> skin
                                                               (skin/font "default-font")
                                                               bitmap-font/data
                                                               (font.data/set-markup-enabled! true))
                                                           skin)
                                               :ctx/unit-scale (atom 1)
                                               :ctx/textures (com.badlogic.gdx.textures/create)})
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
                          (.setForegroundFPS foreground-fps)))))
