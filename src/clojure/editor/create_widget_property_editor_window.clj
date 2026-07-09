(ns clojure.editor.create-widget-property-editor-window
  (:require [clojure.scene2d.actor.get-stage]
            [clojure.ui.error-window :as error-window]
            [clojure.scene2d.actor.remove-actor]
            [clojure.scene2d.actor.find-ancestor :refer [find-ancestor]]
            [clojure.set-ctx :as set-ctx]
            [clojure.stage :as stage]
            [clojure.throwable :as throwable]
            [clojure.scene2d.actor.set-name]
            [clojure.ui.window.add-close-button :as add-close-button]
            [clojure.scene2d.actor.add-listener]
            [clojure.db.update :refer [update!]]
            [clojure.db.delete :refer [delete!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.widget-value :refer [widget-value]]
            [clojure.event :as event]
            [clojure.scene2d.group :as group]
            [clojure.input.key-just-pressed :as key-just-pressed?]
            [clojure.scene2d-actor :as actor]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.type :refer [property->type]]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.ui-window :as window]
            [clojure.scene2d.utils.change-listener :as change-listener]
            [clojure.window :as gdx-window]))

(defn with-window-close [f]
  (fn [actor {:keys [ctx/skin
                     ctx/stage]
              :as ctx}]
    (try
     (let [new-ctx (update ctx :ctx/db f)
           stage (clojure.scene2d.actor.get-stage/f actor)]
       (set-ctx/f stage new-ctx))
     (clojure.scene2d.actor.remove-actor/f (find-ancestor actor (partial instance? gdx-window/class)))
     (catch Throwable t
       (throwable/pretty-pst t)
       (stage/add-actor! stage
                         (error-window/create
                          {:type :ui/error-window
                           :skin skin
                           :throwable t}))))))

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
        clicked-delete-fn (with-window-close (fn [db]
                                               (delete! db property-id)))
        clicked-save-fn (with-window-close (fn [db]
                                             (update! db (get-widget-value))))
        scroll-pane-rows [[{:actor widget :colspan 2}]
                          [{:actor (doto (text-button/create
                                          {:text "Save [LIGHT_GRAY](ENTER)[]"
                                           :skin skin})
                                     (clojure.scene2d.actor.add-listener/f (change-listener/create
                                                              (fn [event actor]
                                                                (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (clojure.scene2d.actor.add-listener/f (change-listener/create
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
                                  (when-let [stage (clojure.scene2d.actor.get-stage/f this)]
                                    (let [ctx (:stage/ctx stage)]
                                      (when (key-just-pressed?/f (:ctx/input ctx) :input.keys/enter)
                                        (clicked-save-fn this ctx)))))}))
      (clojure.scene2d.actor.set-name/f "moon.ui.clojure.editor-window"))))
