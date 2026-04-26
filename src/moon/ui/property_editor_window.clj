(ns moon.ui.property-editor-window
  (:require [clojure.scene2d.event :as event]
            [clojure.graphics.viewport :as viewport]
            [clojure.scene2d.actor :as actor]
            [moon.db :as db]
            [moon.input :as input]
            [moon.property :as property]
            [moon.schema :as schema]
            [clojure.gdx.scene2d.stage :as stage]
            [moon.throwable :as throwable]))

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
                                                   (actor/create
                                                    {:type :ui/error-window
                                                     :skin skin
                                                     :throwable t}))))))
        clicked-delete-fn (with-window-close (fn [db]
                                               (db/delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (db/update! db (get-widget-value))))
        actors [(actor/create
                 {:type :ui/actor
                  :act! (fn [this delta]
                          (when-let [stage (actor/stage this)]
                            (let [{:keys [ctx/input] :as ctx} (stage/ctx stage)]
                              (when (input/key-just-pressed? input :input.keys/enter)
                                (clicked-save-fn this ctx)))))})]
        save-button {:type :ui/text-button
                     :text "Save [LIGHT_GRAY](ENTER)[]"
                     :skin skin
                     :actor/listeners [[:listener/change
                                        (fn [event actor]
                                          (clicked-save-fn actor (stage/ctx (event/stage event))))]]}
        delete-button {:type :ui/text-button
                       :text "Delete"
                       :skin skin
                       :actor/listeners [[:listener/change
                                          (fn [event actor]
                                            (clicked-delete-fn actor (stage/ctx (event/stage event))))]]}
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (actor/create save-button) :center? true}
                           {:actor (actor/create delete-button) :center? true}]]
        rows [[(let [table (actor/create
                            {:type :ui/table
                             :table/cell-defaults {:pad 5}
                             :table/rows scroll-pane-rows})]
                 {:actor (actor/create {:type :ui/scroll-pane
                                        :actor table
                                        :skin skin})
                  :width  (+ (actor/width table) 50)
                  :height (min (- scroll-pane-height 50)
                               (actor/height table))})]]]
    (actor/create
     {:type :ui/window
      :title "[SKY]Property[]"
      :skin skin
      :window/close-button? skin
      :window/modal? true
      :table/cell-defaults {:pad 5}
      :table/rows rows
      :group/actors actors
      :actor/name "moon.ui.editor.window"})))
