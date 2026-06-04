(ns editor.map-widget-table
  (:require [clojure.core.interpose-f :refer [interpose-f]]
            [clojure.gdx.scene2d.event.get-stage :refer [get-stage]]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scene2d.actor.set-name :refer [set-name!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.ui.table.add-rows :refer [add-rows!]]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]))

(defn- horiz-sep [colspan]
  (fn []
    [{:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "default")
      :pad-top 2
      :pad-bottom 2
      :colspan colspan
      :fill-x? true
      :expand-x? true}]))

(defn create
  [{:keys [create-component-row
           skin
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
                                           (create-component-row
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
                                                           ctx/skin
                                                           ctx/add-component-window]} (:stage/ctx (get-stage event))]
                                               (add-actor!
                                                stage
                                                (add-component-window
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
