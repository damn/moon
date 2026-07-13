(ns moon.dev-menu
  (:require [clojure.gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [clojure.gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.scenes.scene2d.group :as group]
            [clojure.gdx.scenes.scene2d.touchable :as touchable]
            [clojure.gdx.scenes.scene2d.ui.image :as image]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn- set-label-text-actor [label-widget text-fn]
  (actor/new
   (fn [this _delta]
     (when-let [stage (actor/get-stage this)]
       (label/set-text! label-widget (text-fn (:stage/ctx stage)))))
   (fn [_actor _batch _parent-alpha])))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create "" skin)
         sub-table (table/create {:table/rows [[{:actor (image/create-from-texture icon)}
                                                              label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add-cell! table {:actor sub-table
                       :right? true
                       :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/create "" skin)]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add-cell! table {:actor label
                       :right? true
                       :expand-x? true}))))

(defn- main-table [skin menus update-labels]
  (let [table (table/create {:table/rows [(for [{:keys [label items]} menus]
                                                           {:actor
                                                            (doto (text-button/create label skin)
                                                              (actor/add-listener! (change-listener/create
                                                                                  (fn [event actor]
                                                                                    (stage/add-actor! (event/get-stage event)
                                                                                                    (window/create {:title label
                                                                                                                    :skin skin
                                                                                                                    :table/rows [(for [{:keys [label on-click]} items]
                                                                                                                                  {:actor
                                                                                                                                   (doto (text-button/create label skin)
                                                                                                                                     (actor/add-listener! (change-listener/create
                                                                                                                                                           (fn [event actor]
                                                                                                                                                             (let [stage (event/get-stage event)]
                                                                                                                                                               (stage/set-ctx! stage
                                                                                                                                                                               (on-click (:stage/ctx stage))))))))})]
                                                                                                                    :window/add-close-button? true}))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (table/create {:table/rows [[{:actor (main-table skin menus update-labels)
                                                           :expand-x? true
                                                           :fill-x? true
                                                           :colspan 1}]
                                                         [{:actor (doto (label/create "" skin)
                                                                        (actor/set-touchable! touchable/disabled))
                                                           :expand? true
                                                           :fill-x? true
                                                           :fill-y? true}]]})
        (layout/setFillParent true)))
