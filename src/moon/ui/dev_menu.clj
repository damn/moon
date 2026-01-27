(ns moon.ui.dev-menu
  (:require [moon.ui :as ui])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Image
                                               Skin
                                               Table
                                               TextButton)
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
   (let [label (ui/actor
                {:type :ui/label
                 :label/text ""
                 :label/skin skin})
         sub-table (ui/actor
                    {:type :ui/table
                     :rows [[{:actor (Image. ^Texture icon)}
                             label]]})]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor sub-table)))))
  ([skin ^Table table text-fn]
   (let [label (ui/actor
                {:type :ui/label
                 :label/text ""
                 :label/skin skin})]
     (.addActor table (set-label-text-actor label text-fn))
     (.expandX (.right (.add table ^Actor label))))))

(defn- create-window [skin label items]
  (ui/actor
   {:type :ui/window
    :skin skin
    :pack? true
    :title label
    :rows [(for [{:keys [label on-click]} items]
             {:actor (doto (TextButton. ^String label ^Skin skin)
                       (.addListener
                        (proxy [ChangeListener] []
                          (changed [event actor]
                            (on-click actor (.ctx ^Stage (Event/.getStage event)))))))})]}))

(defn- main-table [^Skin skin menus update-labels]
  (let [table (ui/actor
               {:type :ui/table
                :rows [(for [{:keys [label items]} menus]
                         {:actor (doto (TextButton. ^String label skin)
                                   (.addListener
                                    (proxy [ChangeListener] []

                                      (changed [event actor]
                                        (Stage/.addActor (Event/.getStage event) (create-window skin label items))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (ui/actor
   {:type :ui/table
    :rows [[{:actor (main-table skin menus update-labels)
             :expand-x? true
             :fill-x? true
             :colspan 1}]
           [{:actor (doto (ui/actor
                           {:type :ui/label
                            :label/text ""
                            :label/skin skin})
                      (Actor/.setTouchable Touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
