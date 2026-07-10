(ns clojure.editor.create-widget-map-widget-table-create
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget-add-component-window :as add-component-window]
            [clojure.editor.create-widget-create-component-row :as create-component-row]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.horiz-sep :as horiz-sep]
            [clojure.interpose-f :refer [interpose-f]]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn map-widget-table-create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (doto (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))
                (actor/setName "moon.db.schema.map.ui.widget"))
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
                [{:actor (doto (text-button/new "Add component" skin)
                           (actor/addListener (change-listener/create
                                                    (fn [event actor]
                                                      (let [{:keys [ctx/db
                                                                    ctx/stage
                                                                    ctx/skin]} (:stage/ctx (event/getStage event))]
                                                        (stage/addActor
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
