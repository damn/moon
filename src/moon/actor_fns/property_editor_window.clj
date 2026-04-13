(ns moon.actor-fns.property-editor-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as gdx-window]
            [gdl.scene2d.event :as event]
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
            [moon.ui :as ui]
            [moon.window :as window]))

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
                               (actor/remove! (actor/find-ancestor actor :ui/window))
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
        actors [(ui/create
                 {:type :ui/actor
                  :act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [{:keys [ctx/input] :as ctx} (stage/ctx stage)]
                              (when (input/key-just-pressed? input :input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button {:type :ui/text-button
                     :text "Save [LIGHT_GRAY](ENTER)[]"
                     :skin skin
                     :actor/listener [:listener/change
                                      (fn [event actor]
                                        (clicked-save-fn actor (stage/ctx (event/stage event))))]}
        delete-button {:type :ui/text-button
                       :text "Delete"
                       :skin skin
                       :actor/listener [:listener/change
                                        (fn [event actor]
                                          (clicked-delete-fn actor (stage/ctx (event/stage event))))]}
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (ui/create save-button) :center? true}
                           {:actor (ui/create delete-button) :center? true}]]
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
