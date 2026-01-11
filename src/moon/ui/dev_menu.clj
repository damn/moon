(ns moon.ui.dev-menu
  (:require [moon.ui.actor :as actor]
            [moon.ui.cell :as cell]
            [moon.ui.group :as group]
            [moon.ui.label :as label]
            [moon.ui.stage :as stage]
            [moon.ui.text-button :as text-button]
            [moon.ui.image :as image]
            [moon.ui.table :as table]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Event
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:act (fn [this delta]
           (when-let [stage (actor/stage this)]
             (.setText label (text-fn (stage/ctx stage)))))
    :draw (fn [this batch parent-alpha])}))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create "" skin)
         sub-table (table/create
                    {:rows [[{:actor (image/create {:image/object icon})}
                             label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table sub-table)))))
  ([skin table text-fn]
   (let [label (label/create "" skin)]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table label))))))

(defn- create-window [skin label items]
  (window/create
   {:skin skin
    :pack? true
    :title label
    :rows [(for [{:keys [label on-click]} items]
             {:actor (doto (text-button/create label skin)
                       (actor/add-listener!
                        (proxy [ChangeListener] []
                          (changed [event actor]
                            (on-click actor (stage/ctx (Event/.getStage event)))))))})]}))

(defn- main-table [skin menus update-labels]
  (let [table (table/create
               {:rows [(for [{:keys [label items]} menus]
                         {:actor (doto (text-button/create label skin)
                                   (actor/add-listener!
                                    (change-listener/create
                                     (fn [event actor]
                                       (.addActor (Event/.getStage event) (create-window skin label items))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (table/create
   {:rows [[{:actor (main-table skin menus update-labels)
             :expand-x? true
             :fill-x? true
             :colspan 1}]
           [{:actor (doto (label/create "" skin)
                      (actor/set-touchable! Touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
