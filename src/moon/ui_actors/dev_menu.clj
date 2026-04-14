(ns moon.ui-actors.dev-menu
  (:require [gdl.scene2d.event :as event]
            [gdl.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [moon.actor :as actor]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.ui :as ui]))

(defn- set-label-text-actor [label text-fn]
  (ui/create
   {:type :ui/actor
    :act! (fn [this _delta]
            (when-let [stage (actor/stage this)]
              (label/set-text! label (text-fn (stage/ctx stage)))))}))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create "" skin)
         sub-table (ui/create
                    {:type :ui/table
                     :table/rows [[{:actor (image/create icon)}
                                   label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (gdx-table/add! table sub-table)))))
  ([skin table text-fn]
   (let [label (label/create "" skin)]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (gdx-table/add! table label))))))

(defn- create-window [skin label items]
  (ui/create
   {:type :ui/window
    :title label
    :skin skin
    :window/close-button? skin
    :table/rows [(for [{:keys [label on-click]} items]
                   {:actor
                    (ui/create
                     {:type :ui/text-button
                      :text label
                      :skin skin
                      :actor/listeners {:listener/change (fn [event actor]
                                                           (on-click actor (stage/ctx (event/stage event))))}})})]}))

(defn- main-table [skin menus update-labels]
  (let [table (ui/create
               {:type :ui/table
                :table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (ui/create
                                 {:type :ui/text-button
                                  :text label
                                  :skin skin
                                  :actor/listeners {:listener/change (fn [event actor]
                                                                       (stage/add-actor! (event/stage event) (create-window skin label items)))}})})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn- create*
  [{:keys [menus update-labels skin]}]
  (doto (ui/create
         {:type :ui/table
          :table/rows [[{:actor (main-table skin menus update-labels)
                         :expand-x? true
                         :fill-x? true
                         :colspan 1}]
                       [{:actor (ui/create
                                 {:type :ui/label
                                  :text ""
                                  :skin skin
                                  :actor/touchable :touchable/disabled})
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (widget-group/set-fill-parent! true)))

(defn create
  [{:keys [ctx/skin
           ctx/textures]
    :as ctx}
   {:keys [update-labels
           menus]}]
  (create*
   {:menus (for [create-fn menus]
             (create-fn ctx))
    :update-labels (for [item (map deref update-labels)]
                     (if (:icon item)
                       (update item :icon #(get textures %))
                       item))
    :skin skin}))
