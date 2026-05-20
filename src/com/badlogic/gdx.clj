(ns com.badlogic.gdx
  (:require [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.image]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.widget-group]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.utils.align :as align]
            [com.badlogic.gdx.utils.screen-utils :as screen-utils]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [gdl.app :as app]
            [gdl.application-listener :as listener]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.graphics.g2d.texture-region :as texture-region]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.input :as input]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.event :as event]
            [gdl.scene2d.ui.check-box :as check-box]
            [gdl.scene2d.ui.image :as image]
            [gdl.scene2d.ui.label :as label]
            [gdl.scene2d.ui.select-box :as select-box]
            [gdl.scene2d.ui.skin :as skin]
            [gdl.scene2d.ui.table]
            [gdl.scene2d.ui.text-field]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [gdl.sound :as sound]
            [gdl.utils.disposable :as disposable])
  (:import (com.badlogic.gdx Application
                             ApplicationListener
                             Audio
                             Files
                             Gdx
                             Graphics
                             Input)
           (com.badlogic.gdx.audio Sound)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)
           (com.badlogic.gdx.graphics.g2d BitmapFont
                                          BitmapFont$BitmapFontData
                                          TextureRegion)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Actor
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
           (com.badlogic.gdx.utils Disposable)))

(def clear-screen! screen-utils/clear!)

(def fit-viewport fit-viewport/create)

(def tooltip-manager-set-initial-time! tooltip-manager/set-initial-time!)

(def put-colors! colors/put!)

(def pixmap pixmap/create)

(def orthographic-camera orthographic-camera/create)

(def sprite-batch sprite-batch/create)

(def stage ctx-stage/create)

(defn application!
  [listener
   {:keys [title
           windowed-mode
           foreground-fps]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (listener/create! listener Gdx/app))

                        (dispose [_]
                          (listener/dispose! listener))

                        (render [_]
                          (listener/render! listener))

                        (resize [_ width height]
                          (listener/resize! listener width height))

                        (pause [_]
                          (listener/pause! listener))

                        (resume [_]
                          (listener/resume! listener)))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))

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
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Files
  files/Files
  (internal [this path]
    (.internal this path)))

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

(extend-type Sound
  sound/Sound
  (play! [this]
    (.play this)))

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
           wrap?))

  (set-use-integer-positions! [font use-integer-positions?]
    (.setUseIntegerPositions font use-integer-positions?)))

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

(extend-type Disposable
  disposable/Disposable
  (dispose! [this]
    (.dispose this)))

(extend-type Event
  event/Event
  (stage [event]
    (.getStage event)))

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

(defmethod actor/create :ui/label
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type Label
  label/Label
  (set-text! [label text]
    (.setText label ^String text)))

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

(defmethod actor/create :ui/check-box
  [{:keys [skin checked?]}]
  (doto (CheckBox. "" ^Skin skin)
    (.setChecked checked?)))

(extend-type CheckBox
  check-box/CheckBox
  (checked? [this]
    (.isChecked this)))

(extend-type FileHandle
  file-handle/FileHandle
  (list [this]
    (.list this))
  (path [this]
    (.path this))
  (extension [this]
    (.extension this))
  (directory? [this]
    (.isDirectory this))
  (texture [this]
    (Texture. this))
  (pixmap [this]
    (Pixmap. this))
  (skin [this]
    (Skin. this))
  (freetype-font-generator [this]
    (FreeTypeFontGenerator. this)))

(extend-type FreeTypeFontGenerator
  font-generator/FreeTypeFontGenerator
  (generate-font [this {:keys [size
                               min-filter
                               mag-filter]}]
    (.generateFont this (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                          (set! (.size params) size)
                          (set! (.minFilter params) min-filter)
                          (set! (.magFilter params) mag-filter)
                          params)))

  (dispose! [this]
    (.dispose this)))
