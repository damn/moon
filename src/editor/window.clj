(ns editor.window
  (:require [clojure.gdx :as gdx]
            [scene2d.actor :as actor]
            [scene2d.utils.change-listener :as change-listener]
            [input.key-just-pressed :as key-just-pressed?]
            [scene2d.ui.table.scroll-pane-cell :as scroll-pane-cell]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [editor.window.with-window-close :as with-window-close]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.db.delete :refer [delete!]]
            [moon.db.update :refer [update!]]
            [moon.property.type :refer [property->type]]
            [editor.create-widget :as create-widget]
            [editor.widget-value :as widget-value]))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property->type property))
        widget (create-widget/f schema property ctx) ; FIXME here no set user object k v ? (widget/build)
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget-value/f schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close/f (fn [db]
                                                 (delete! db property-id)))
        clicked-save-fn (with-window-close/f (fn [db]
                                               (update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create
                                          {:text "Save [LIGHT_GRAY](ENTER)[]"
                                           :skin skin})
                                     (gdx/add-listener! (change-listener/create
                                                         (fn [event actor]
                                                           (clicked-save-fn actor (:stage/ctx (gdx/event-get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (gdx/add-listener! (change-listener/create
                                                         (fn [event actor]
                                                           (clicked-delete-fn actor (:stage/ctx (gdx/event-get-stage event)))))))
                            :center? true}]]]
    (doto (window/create
           {:title "[SKY]Property[]"
            :skin skin
            :table/cell-defaults {:pad 5}
            :table/rows [[(scroll-pane-cell/create
                           (table/create {:table/cell-defaults {:pad 5}
                                          :table/rows scroll-pane-rows})
                           skin
                           scroll-pane-height
                           50)]]})
      (add-close-button/f! skin)
      (gdx/window-set-modal! true)
      (gdx/add-actor! (actor/f
                       {:act! (fn [this delta]
                                (when-let [stage (gdx/get-stage this)]
                                  (let [ctx (:stage/ctx stage)]
                                    (when (key-just-pressed?/f (:ctx/input ctx) :input.keys/enter)
                                      (clicked-save-fn this ctx)))))}))
      (gdx/set-name! "moon.ui.editor.window"))))
