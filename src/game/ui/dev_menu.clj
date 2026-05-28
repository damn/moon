(ns game.ui.dev-menu
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.scenes.scene2d.group :as group]
            [clojure.gdx.scenes.scene2d.ui.image :as image]
            [clojure.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clojure.scene2d.stage :as stage]
            [clojure.gdx.scenes.scene2d.ui.label :as label]
            [clojure.scene2d.ui.table :as table]))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:type :ui/actor
    :act! (fn [this _delta]
            (when-let [stage (actor/stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (label/create
                {:text ""
                 :skin skin})
         sub-table (actor/create
                    {:type :ui/table
                     :table/rows [[{:actor (image/create {:content icon})}
                                   label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add! table {:actor sub-table
                        :right? true
                        :expand-x? true})))
  ([skin table text-fn]
   (let [label (label/create
                {:text ""
                 :skin skin})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add! table {:actor label
                        :right? true
                        :expand-x? true}))))

(defn- create-window [skin label items]
  {:type :ui/window
   :title label
   :skin skin
   :window/close-button? skin
   :table/rows [(for [{:keys [label on-click]} items]
                  {:actor
                   (text-button/create
                    {:text label
                     :skin skin
                     :actor/listeners {:listener/change (fn [event actor]
                                                          (on-click actor (:stage/ctx (event/stage event))))}})})]})

(defn- main-table [skin menus update-labels]
  (let [table (actor/create
               {:type :ui/table
                :table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (text-button/create
                                 {:text label
                                  :skin skin
                                  :actor/listeners {:listener/change (fn [event actor]
                                                                       (stage/add-actor! (event/stage event)
                                                                                         (actor/create (create-window skin label items))))}})})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! skin table update-fn icon)
          (add-upd-label! skin table update-fn))))
    table))

(defmethod actor/create :ui/menu
  [{:keys [menus update-labels skin]}]
  (actor/create
   {:type :ui/table
    :table/rows [[{:actor (main-table skin menus update-labels)
                   :expand-x? true
                   :fill-x? true
                   :colspan 1}]
                 [{:actor (label/create
                           {:text ""
                            :skin skin
                            :actor/touchable :touchable/disabled})
                   :expand? true
                   :fill-x? true
                   :fill-y? true}]]
    :widget-group/fill-parent? true}))
