(ns editor.map-widget-table
  (:require [clojure.gdx.scene2d.event :as event]
            [editor.map-widget-table.add-component-window :as add-component-window]
            [editor.map-widget-table.component-row :as component-row]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.actor :refer [set-name!
                                               add-listener!]]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.stage :as stage]))

(defn- horiz-sep [colspan]
  (fn []
    [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
      :pad-top 2
      :pad-bottom 2
      :colspan colspan
      :fill-x? true
      :expand-x? true}]))

(defn- interpose-f [f coll] ; TODO use interpose?
  (drop 1 (interleave (repeatedly f) coll)))

(defn create
  [{:keys [skin
           schema
           k->widget
           k->optional?
           ks-sorted
           opt?]}]
  (let [table (doto (table/create
                     {:table/cell-defaults {:pad 5}})
                (set-name! "moon.db.schema.map.ui.widget"))
        colspan 3
        component-rows (interpose-f (horiz-sep colspan)
                                    (map (fn [k]
                                           (component-row/create
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
                           (add-listener! (change-listener/create
                                           (fn [event actor]
                                             (let [{:keys [ctx/db
                                                           ctx/stage
                                                           ctx/skin]} (:stage/ctx (event/stage event))]
                                               (stage/add-actor!
                                                stage
                                                (add-component-window/f
                                                 {:skin skin
                                                  :schemas (:db/schemas db)
                                                  :schema schema
                                                  :map-widget-table table})))))))
                  :colspan colspan}])]
             [(when opt?
                [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
                  :pad-top 2
                  :pad-bottom 2
                  :colspan colspan
                  :fill-x? true
                  :expand-x? true}])]
             component-rows))
    table))
