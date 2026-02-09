(ns moon.ui.property-editor-window
  (:require [moon.db :as db]
            [moon.input :as input]
            [moon.property :as property]
            [moon.schema :as schema]
            [moon.throwable :as throwable]
            [moon.ui.actor :as actor]
            [moon.ui.error-window :as error-window]
            [moon.ui.group :as group]
            [moon.ui.scroll-pane-cell :as scroll-pane-cell]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx Input$Keys)
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

(defn create
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property/type property))
        ; build for get-widget-value
        ; or find a way to find the widget from the context @ save button
        ; should be possible
        widget (schema/create schema property ctx) ; FIXME here no set user object k v ?
        scroll-pane-height (Viewport/.getWorldHeight (Stage/.getViewport stage))
        get-widget-value #(schema/value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [^Actor actor {:keys [ctx/skin
                                                      ctx/stage]
                                               :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     ^Stage stage (.getStage actor)]
                                 (set! (.ctx stage) new-ctx))
                               (Actor/.remove (actor/find-ancestor actor Window))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (Stage/.addActor stage
                                                  (error-window/create
                                                   {:skin skin
                                                    :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(proxy [Actor] []
                  (act [delta]
                    (when-let [^Stage stage (Actor/.getStage this)]
                      (let [{:keys [ctx/input]
                             :as ctx} (.ctx stage)]
                        (when (input/key-just-pressed? input Input$Keys/ENTER)
                          (clicked-save-fn this ctx))))
                    (let [^Actor this this]
                      (proxy-super act delta))))]
        save-button (text-button/create
                     {:text "Save [LIGHT_GRAY](ENTER)[]"
                      :on-clicked clicked-save-fn
                      :skin skin})
        delete-button (text-button/create
                       {:text "Delete"
                        :on-clicked clicked-delete-fn
                        :skin skin})
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor save-button :center? true}
                           {:actor delete-button :center? true}]]
        rows [[(scroll-pane-cell/create skin
                                        scroll-pane-height
                                        scroll-pane-rows)]]]
    (doto (Window. "[SKY]Property[]" ^Skin skin)
      (window/add-close-button! skin)
      (table/set-opts! {:rows rows
                        :cell-defaults {:pad 5}})
      (.setModal true)
      (.pack)
      (group/add-actors! actors)
      (.setName "moon.ui.editor.window"))))
