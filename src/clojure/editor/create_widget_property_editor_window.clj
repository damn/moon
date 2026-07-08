(ns clojure.editor.create-widget-property-editor-window
  (:require [clojure.actor.get-stage]
            [clojure.actor.set-name]
            [clojure.window.add-close-button :as add-close-button]
            [clojure.actor.add-listener]
            [clojure.db-update :refer [update!]]
            [clojure.delete :refer [delete!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.widget-value :refer [widget-value]]
            [clojure.event :as event]
            [clojure.group :as group]
            [clojure.key-just-pressed :as key-just-pressed?]
            [clojure.scene2d-actor :as actor]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.type :refer [property->type]]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.utils-change-listener :as change-listener]
            [clojure.window :as gdx-window]
            [clojure.with-window-close :as with-window-close]))

(defn property-editor-window
  [{:keys [ctx
           property]}]
  (let [{:keys [ctx/db
                ctx/skin
                ctx/stage]} ctx
        schemas (:db/schemas db)
        schema (get schemas (property->type property))
        widget (create-widget schema property ctx)
        scroll-pane-height (:viewport/world-height (:stage/viewport stage))
        get-widget-value #(widget-value schema widget schemas)
        property-id (:property/id property)
        clicked-delete-fn (with-window-close/f (fn [db]
                                                 (delete! db property-id)))
        clicked-save-fn (with-window-close/f (fn [db]
                                               (update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create
                                          {:text "Save [LIGHT_GRAY](ENTER)[]"
                                           :skin skin})
                                     (clojure.actor.add-listener/f (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (clojure.actor.add-listener/f (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-delete-fn actor (:stage/ctx (event/get-stage event)))))))
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
      (gdx-window/set-modal! true)
      (group/add-actor! (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (clojure.actor.get-stage/f this)]
                                    (let [ctx (:stage/ctx stage)]
                                      (when (key-just-pressed?/f (:ctx/input ctx) :input.keys/enter)
                                        (clicked-save-fn this ctx)))))}))
      (clojure.actor.set-name/f "moon.ui.clojure.editor-window"))))
