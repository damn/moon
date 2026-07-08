(ns clojure.editor.create-widget-map-widget-table-create
  (:require [clojure.actor.set-name]
            [clojure.actor.add-listener]
            [clojure.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget-add-component-window :as add-component-window]
            [clojure.editor.create-widget-create-component-row :as create-component-row]
            [clojure.event :as event]
            [clojure.horiz-sep :as horiz-sep]
            [clojure.interpose-f :refer [interpose-f]]
            [clojure.stage :as stage]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

(defn map-widget-table-create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (doto (table/create
                     {:table/cell-defaults {:pad 5}})
                (clojure.actor.set-name/f "moon.db.schema.map.ui.widget"))
        colspan 3
        component-rows (interpose-f (horiz-sep/f colspan)
                                    (map (fn [k]
                                           (create-component-row/create-component-row
                                            {:skin skin
                                             :editor-widget (k->widget k)
                                             :k k
                                             :display-remove-component-button? (k->optional? k)
                                             :table table}))
                                         ks-sorted))]
    (add-rows!
     table
     (concat [(when opt?
                [{:actor (doto (text-button/create
                                {:text "Add component"
                                 :skin skin})
                           (clojure.actor.add-listener/f (change-listener/create
                                                    (fn [event actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/stage
                                                                    ctx/skin]} (:stage/ctx (event/get-stage event))]
                                                        (stage/add-actor!
                                                         stage
                                                         (add-component-window/add-component-window
                                                          {:skin skin
                                                           :schemas (:db/schemas db)
                                                           :schema schema
                                                           :map-widget-table table})))))))
                  :colspan colspan}])]
             [(when opt?
                [{:actor nil
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))
