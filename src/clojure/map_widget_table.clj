(ns clojure.map-widget-table
  (:require
            [clojure.add-listener]
            [clojure.set-name] [clojure.stage :as stage]
            [clojure.event :as event]
            [clojure.interpose-f :refer [interpose-f]]
            [clojure.ui-table :as table]
            [clojure.add-rows :refer [add-rows!]]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]
            [clojure.horiz-sep :as horiz-sep]))

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
                (clojure.set-name/f "moon.db.schema.map.ui.widget"))
        colspan 3
        component-rows (interpose-f (horiz-sep/f colspan)
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
                           (clojure.add-listener/f (change-listener/create
                                            (fn [event actor]
                                              (let [{:keys [ctx/db
                                                            ctx/stage
                                                            ctx/skin
                                                            ctx/add-component-window]} (:stage/ctx (event/get-stage event))]
                                                (stage/add-actor!
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
