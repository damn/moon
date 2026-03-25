(ns moon.ui-actors.dev-menu
  (:require [moon.table :as table]
            [moon.window :as window])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Label
                                               Skin
                                               Table
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (moon Stage)))

(defn- set-label-text-actor [label text-fn]
  (proxy [Actor] []
    (act [delta]
      (when-let [stage (Actor/.getStage this)]
        (Label/.setText label ^String (text-fn (.ctx ^Stage stage))))
      (let [^Actor this this]
        (proxy-super act delta)))))

(defn add-upd-label!
  ([skin ^Table table text-fn icon]
   (let [label (Label. "" ^Skin skin)
         sub-table (doto (Table.)
                     (table/add-rows!
                      [[{:actor (Image. ^Texture icon)}
                        label]]))]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor sub-table)))))
  ([skin ^Table table text-fn]
   (let [label (Label. "" ^Skin skin)]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor label))))))

(defn- create-window [^Skin skin ^String label items]
  (doto (Window. label skin)
    (window/add-close-button! skin)
    (table/add-rows! [(for [{:keys [^String label on-click]} items]
                        {:actor (doto (TextButton. label skin)
                                  (.addListener
                                   (proxy [ChangeListener] []
                                     (changed [event actor]
                                       (on-click actor (.ctx ^Stage (Event/.getStage event)))))))})])
    (.pack)))

(defn- main-table [^Skin skin menus update-labels]
  (let [table (doto (Table.)
                (table/add-rows!
                 [(for [{:keys [label items]} menus]
                    {:actor (doto (TextButton. ^String label skin)
                              (.addListener
                               (proxy [ChangeListener] []

                                 (changed [event actor]
                                   (Stage/.addActor (Event/.getStage event) (create-window skin label items))))))})]))]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn- create*
  [{:keys [menus update-labels skin]}]
  (doto (Table.)
    (table/add-rows!
     [[{:actor (main-table skin menus update-labels)
        :expand-x? true
        :fill-x? true
        :colspan 1}]
      [{:actor (doto (Label. "" ^Skin skin)
                 (Actor/.setTouchable Touchable/disabled))
        :expand? true
        :fill-x? true
        :fill-y? true}]])
    (.setFillParent true)))

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
