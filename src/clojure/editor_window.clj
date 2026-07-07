(ns clojure.editor-window
  (:require
            [clojure.add-listener]
            [clojure.get-stage]
            [clojure.set-name] [clojure.window :as gdx-window]
            [clojure.group :as group]
            [clojure.event :as event]
            [clojure.create-widget :as create-widget]
            [clojure.widget-value :as widget-value]
            [clojure.with-window-close :as with-window-close]
            [clojure.ui-table :as table]
            [clojure.ui-window :as window]
            [clojure.key-just-pressed :as key-just-pressed?]
            [clojure.delete :refer [delete!]]
            [clojure.db-update :refer [update!]]
            [clojure.type :refer [property->type]]
            [clojure.scene2d-actor :as actor]
            [clojure.scroll-pane-cell :as scroll-pane-cell]
            [clojure.ui-text-button :as text-button]
            [clojure.add-close-button :as add-close-button]
            [clojure.utils-change-listener :as change-listener]))

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
                                     (clojure.add-listener/f (change-listener/create
                                                      (fn [event actor]
                                                        (clicked-save-fn actor (:stage/ctx (event/get-stage event)))))))
                            :center? true}
                           {:actor (doto (text-button/create
                                          {:text "Delete"
                                           :skin skin})
                                     (clojure.add-listener/f (change-listener/create
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
                             (when-let [stage (clojure.get-stage/f this)]
                               (let [ctx (:stage/ctx stage)]
                                 (when (key-just-pressed?/f (:ctx/input ctx) :input.keys/enter)
                                   (clicked-save-fn this ctx)))))}))
      (clojure.set-name/f "moon.ui.clojure.editor-window"))))
