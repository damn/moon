(ns moon.actor-fns.property-editor-window
  (:require [clj.api.com.badlogic.gdx.input.keys :as input.keys]
            [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [clj.api.com.badlogic.gdx.scenes.scene2d.event :as event]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.db :as db]
            [moon.group :as group]
            [moon.input :as input]
            [moon.property :as property]
            [moon.schema :as schema]
            [moon.scroll-pane-cell :as scroll-pane-cell]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.throwable :as throwable]
            [moon.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

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
        scroll-pane-height (viewport/world-height (stage/viewport stage))
        get-widget-value #(schema/value schema widget schemas)
        property-id (:property/id property)
        with-window-close (fn [f]
                            (fn [actor {:keys [ctx/skin
                                               ctx/stage]
                                        :as ctx}]
                              (try
                               (let [new-ctx (update ctx :ctx/db f)
                                     stage (actor/stage actor)]
                                 (stage/set-ctx! stage new-ctx))
                               (actor/remove! (actor/find-ancestor actor Window))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (stage/add-actor! stage
                                                   ((get (:ctx/actor-fns ctx) :ui/error-window)
                                                    {:skin skin
                                                     :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(gdx-actor/create
                 {:act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [{:keys [ctx/input] :as ctx} (stage/ctx stage)]
                              (when (input/key-just-pressed? input input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button (doto (text-button/create "Save [LIGHT_GRAY](ENTER)[]" skin)
                      (actor/add-listener!
                       (change-listener/create
                        (fn [event actor]
                          (clicked-save-fn actor (stage/ctx (event/stage event)))))))
        delete-button (doto (text-button/create "Delete" skin)
                        (actor/add-listener!
                         (change-listener/create
                          (fn [event actor]
                            (clicked-delete-fn actor (stage/ctx (event/stage event)))))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor save-button :center? true}
                           {:actor delete-button :center? true}]]
        rows [[(scroll-pane-cell/create skin
                                        scroll-pane-height
                                        scroll-pane-rows)]]]
    (doto (gdx-window/create "[SKY]Property[]" skin)
      (window/add-close-button! skin)
      (table/set-cell-defaults! {:pad 5})
      (table/add-rows! rows)
      (gdx-window/set-modal! true)
      (widget-group/pack!)
      (group/add-actors! actors)
      (actor/set-name! "moon.ui.editor.window"))))
