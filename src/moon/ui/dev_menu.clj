(ns moon.ui.dev-menu
  (:require [moon.stage :as stage]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Event
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn- set-label-text-actor [label text-fn]
  (proxy [Actor] []
    (act [delta]
      (when-let [stage (Actor/.getStage this)]
        (.setText label (text-fn (stage/ctx stage))))
      (let [^Actor this this]
        (proxy-super act delta)))))

(defn add-upd-label!
  ([skin table text-fn icon]
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
  ([skin table text-fn]
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
             {:actor (doto (TextButton. label ^Skin skin)
                       (.addListener
                        (proxy [ChangeListener] []
                          (changed [event actor]
                            (on-click actor (.ctx (Event/.getStage event)))))))})]}))

(defn- main-table [^Skin skin menus update-labels]
  (let [table (ui/actor
               {:type :ui/table
                :rows [(for [{:keys [label items]} menus]
                         {:actor (doto (TextButton. label skin)
                                   (.addListener
                                    (proxy [ChangeListener] []

                                      (changed [event actor]
                                        (stage/add-actor! (Event/.getStage event) (create-window skin label items))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  "Input: menus is : .. update-labels is: .. Skin is ..
  Returns a table ui actor with ..."
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
                      (.setTouchable Touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
