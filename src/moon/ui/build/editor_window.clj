(ns moon.ui.build.editor-window
  (:require [gdl.input.keys :as input.keys]
            [gdl.ui.actor :as actor]
            [gdl.ui.stage :as stage]
            [moon.db :as db]
            [moon.db.property :as property]
            [moon.input :as input]
            [moon.throwable :as throwable]
            [moon.ui :as ui]
            [moon.ui.editor.schema :as schema]
            [moon.ui.text-button :as text-button]
            [moon.ui.widget :as widget]
            [moon.ui.window :as window]))

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
        scroll-pane-height (ui/viewport-height stage)
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
                               (actor/remove! (window/find-ancestor actor))
                               (catch Throwable t
                                 (throwable/pretty-pst t)
                                 (ui/show-error-window! stage skin t)))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:act (fn [this delta]
                         (when-let [stage (actor/stage this)]
                           (let [{:keys [ctx/input]
                                  :as ctx} (stage/ctx stage)]
                             (when (input/key-just-pressed? input input.keys/enter)
                               (clicked-save-fn this ctx)))))
                  :draw (fn [this batch parent-alpha])})]
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
        rows [[(widget/scroll-pane-cell skin
                                        scroll-pane-height
                                        scroll-pane-rows)]]]
    (window/create
     {:skin skin
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
