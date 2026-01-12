(ns moon.ui.dev-menu
  (:require [moon.ui.image :as image]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Event
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn- set-label-text-actor [label text-fn]
  (proxy [Actor] []
    (act [delta]
      (when-let [stage (.getStage this)]
        (.setText label (text-fn (.ctx stage))))
      (let [^Actor this this]
        (proxy-super act delta)))))

(defn add-upd-label!
  ([skin table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (table/create
                    {:rows [[{:actor (image/create {:image/object icon})}
                             label]]})]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor sub-table)))))
  ([skin table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor label))))))

(defn- create-window [skin label items]
  (window/create
   {:skin skin
    :pack? true
    :title label
    :rows [(for [{:keys [label on-click]} items]
             {:actor (doto (text-button/create label skin)
                       (.addListener
                        (proxy [ChangeListener] []
                          (changed [event actor]
                            (on-click actor (.ctx (Event/.getStage event)))))))})]}))

(defn- main-table [skin menus update-labels]
  (let [table (table/create
               {:rows [(for [{:keys [label items]} menus]
                         {:actor (doto (text-button/create label skin)
                                   (.addListener
                                    (proxy [ChangeListener] []

                                      (changed [event actor]
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
           [{:actor (doto (Label. "" ^Skin skin)
                      (.setTouchable Touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
