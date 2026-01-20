(ns moon.ui.property-editor-window
  (:require [moon.db :as db]
            [moon.input :as input]
            [moon.property :as property]
            [moon.throwable :as throwable]
            [moon.schema :as schema]
            [moon.stage :as stage]
            [moon.ui :as ui]
            [moon.ui.actor :as actor]
            [moon.ui.scroll-pane-cell :as scroll-pane-cell])
  (:import (com.badlogic.gdx Input$Keys)
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Window)
           (com.badlogic.gdx.utils.viewport Viewport)))

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
        widget (schema/create schema property ctx)
        scroll-pane-height (Viewport/.getWorldHeight (stage/viewport stage))
        get-widget-value #(schema/value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [actor {:keys [ctx/skin
                                               ctx/stage]
                                        :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     stage (.getStage actor)]
                                 (set! (.ctx stage) new-ctx))
                               (.remove (actor/find-ancestor actor Window))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (stage/add-actor! stage
                                                   (ui/actor {:type :ui/error-window
                                                              :skin skin
                                                              :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(proxy [Actor] []
                  (act [delta]
                    (when-let [stage (.getStage this)]
                      (let [{:keys [ctx/input]
                             :as ctx} (.ctx stage)]
                        (when (input/key-just-pressed? input Input$Keys/ENTER)
                          (clicked-save-fn this ctx))))
                    (let [^Actor this this]
                      (proxy-super act delta))))]
        save-button (ui/actor
                     {:type :ui/text-button
                      :text "Save [LIGHT_GRAY](ENTER)[]"
                      :on-clicked clicked-save-fn
                      :skin skin})
        delete-button (ui/actor
                       {:type :ui/text-button
                        :text "Delete"
                        :on-clicked clicked-delete-fn
                        :skin skin})
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor save-button :center? true}
                           {:actor delete-button :center? true}]]
        rows [[(scroll-pane-cell/create skin
                                        scroll-pane-height
                                        scroll-pane-rows)]]]
    (ui/actor
     {:type :ui/window
      :skin skin
      :title "[SKY]Property[]"
      :actor/name "moon.ui.editor.window"
      :modal? true
      :close-button? true
      :center? true
      :close-on-escape? true
      :group/actors actors
      :rows rows
      :cell-defaults {:pad 5}
      :pack? true})))
