(ns moon.dev-menu
  (:require [clojure.set-label-text-actor :refer [set-label-text-actor]]
            [moon.stage :as moon-stage]
            [clojure.table-set-opts :as table-set-opts]
            [moon.table :refer [add-cell!]]
            [moon.window :refer [add-close-button!]]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/new "" skin)
         sub-table (doto (table/new)
                     (table-set-opts/set-opts! {:table/rows [[{:actor (image/newTexture icon)}
                                                              label]]}))]
     (group/addActor table (set-label-text-actor label text-fn))
     (add-cell! table {:actor sub-table
                       :right? true
                       :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/new "" skin)]
     (group/addActor table (set-label-text-actor label text-fn))
     (add-cell! table {:actor label
                       :right? true
                       :expand-x? true}))))

(defn- main-table [skin menus update-labels]
  (let [table (doto (table/new)
                (table-set-opts/set-opts! {:table/rows [(for [{:keys [label items]} menus]
                                                           {:actor
                                                            (doto (text-button/new label skin)
                                                              (actor/addListener (change-listener/create
                                                                                  (fn [event actor]
                                                                                    (stage/addActor (event/getStage event)
                                                                                                    (doto (doto (window/new label skin)
                                                                                                                (table-set-opts/set-opts! {:title label
                                                                                                                                           :skin skin
                                                                                                                                           :table/rows [(for [{:keys [label on-click]} items]
                                                                                                                                                         {:actor
                                                                                                                                                          (doto (text-button/new label skin)
                                                                                                                                                            (actor/addListener (change-listener/create
                                                                                                                                                                                (fn [event actor]
                                                                                                                                                                                  (let [stage (event/getStage event)]
                                                                                                                                                                                    (moon-stage/set-ctx! stage
                                                                                                                                                                                                         (on-click (:stage/ctx stage))))))))})]}))
                                                                                                              (add-close-button! skin)))))))})]}))]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (doto (table/new)
               (table-set-opts/set-opts! {:table/rows [[{:actor (main-table skin menus update-labels)
                                                           :expand-x? true
                                                           :fill-x? true
                                                           :colspan 1}]
                                                         [{:actor (doto (label/new "" skin)
                                                                        (actor/setTouchable touchable/disabled))
                                                           :expand? true
                                                           :fill-x? true
                                                           :fill-y? true}]]}))
        (layout/setFillParent true)))
