(ns com.badlogic.gdx
  (:require com.badlogic.gdx.application
            com.badlogic.gdx.audio
            com.badlogic.gdx.audio.sound
            com.badlogic.gdx.files
            com.badlogic.gdx.files.file-handle
            com.badlogic.gdx.graphics
            com.badlogic.gdx.graphics.texture
            com.badlogic.gdx.graphics.g2d.bitmap-font
            com.badlogic.gdx.graphics.g2d.bitmap-font.data
            com.badlogic.gdx.graphics.g2d.texture-region
            com.badlogic.gdx.input
            [com.badlogic.gdx.backends.lwjgl3.application :as application]
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            com.badlogic.gdx.scenes.scene2d.actor
            com.badlogic.gdx.scenes.scene2d.event
            [com.badlogic.gdx.scenes.scene2d.ui.image]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            com.badlogic.gdx.scenes.scene2d.ui.skin
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.widget-group]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            com.badlogic.gdx.utils.disposable
            [com.badlogic.gdx.utils.screen-utils :as screen-utils]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.check-box :as check-box]
            [gdl.scene2d.ui.image :as image]
            [gdl.scene2d.ui.label :as label]
            [gdl.scene2d.ui.select-box :as select-box]
            [gdl.scene2d.ui.table]
            [gdl.scene2d.ui.text-field]
            [gdl.scene2d.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d Actor
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
                                               Widget)))

(def clear-screen! screen-utils/clear!)

(def fit-viewport fit-viewport/create)

(def tooltip-manager-set-initial-time! tooltip-manager/set-initial-time!)

(def put-colors! colors/put!)

(def pixmap pixmap/create)

(def orthographic-camera orthographic-camera/create)

(def sprite-batch sprite-batch/create)

(def stage ctx-stage/create)

(def application! application/create)

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
