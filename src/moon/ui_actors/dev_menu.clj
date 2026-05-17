(ns moon.ui-actors.dev-menu
  (:require [moon.ui.actor :as actor]
            [clojure.scene2d.event :as event]
            [moon.ui.group :as group]
            [moon.stage :as stage]
            [moon.ui.label :as label]
            [moon.ui.table :as table]))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:type :ui/actor
    :act! (fn [this _delta]
            (when-let [stage (actor/stage this)]
              (label/set-text! label (text-fn (:stage/ctx stage)))))}))

(defn- add-upd-label!
  ([skin table text-fn icon]
   (let [label (actor/create {:type :ui/label
                              :text ""
                              :skin skin})
         sub-table (actor/create
                    {:type :ui/table
                     :table/rows [[{:actor (actor/create
                                            {:type :ui/image
                                             :content icon})}
                                   label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (table/add! table {:actor sub-table
                        :right? true
                        :expand-x? true})))
  ([skin table text-fn]
   (let [label (actor/create {:type :ui/label
                              :text ""
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
                   (actor/create
                    {:type :ui/text-button
                     :text label
                     :skin skin
                     :actor/listeners {:listener/change (fn [event actor]
                                                          (on-click actor (:stage/ctx (event/stage event))))}})})]})

(defn- main-table [skin menus update-labels]
  (let [table (actor/create
               {:type :ui/table
                :table/rows [(for [{:keys [label items]} menus]
                               {:actor
                                (actor/create
                                 {:type :ui/text-button
                                  :text label
                                  :skin skin
                                  :actor/listeners {:listener/change (fn [event actor]
                                                                       (stage/add-actor! (event/stage event)
                                                                                         (create-window skin label items)))}})})]})]
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
                 [{:actor (actor/create
                           {:type :ui/label
                            :text ""
                            :skin skin
                            :actor/touchable :touchable/disabled})
                   :expand? true
                   :fill-x? true
                   :fill-y? true}]]
    :widget-group/fill-parent? true}))
