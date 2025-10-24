(ns cdq.ui.dev-menu
  (:require [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [clojure.gdx.scene2d.actor :as actor]
            [cdq.ui.menu :as menu]
            [clojure.vis-ui.label :as label]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.vis-ui.text-button :as text-button]
            [clojure.gdx.scene2d.touchable :as touchable]
            [clojure.vis-ui.label :as vis-label]))

(defn- main-table [menus update-labels]
  (let [table (table/create
               {:rows [(for [{:keys [label items]} menus]
                         {:actor (text-button/create label)})
                       ]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (menu/add-upd-label! table update-fn icon)
          (menu/add-upd-label! table update-fn))))
    table))

(defmethod stage/build :actor/dev-menu
  [{:keys [menus update-labels]}]
  (table/create
   {:rows [[{:actor (main-table menus update-labels)
             #_(menu/create
                     {:menus menus
                      :update-labels update-labels})
             :expand-x? true
             :fill-x? true
             :colspan 1}]
           [{:actor (doto (vis-label/create "")
                      (actor/set-touchable! touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
