(ns moon.ui.dev-menu
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.event :as event]
            [gdl.ui.group :as group]
            [gdl.ui.touchable :as touchable]
            [gdl.ui.cell :as cell]
            [gdl.ui.change-listener :as change-listener]
            [gdl.ui.text-button :as text-button]
            [moon.ui :as ui]
            [moon.ui.image :as image]
            [moon.ui.label :as label]
            [moon.ui.label :as vis-label]
            [gdl.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.window :as window]))

(defn- set-label-text-actor [label text-fn]
  (actor/create
   {:act (fn [this delta]
           (when-let [stage (actor/stage this)]
             (.setText label (text-fn (stage/ctx stage)))))
    :draw (fn [this batch parent-alpha])}))

(defn add-upd-label!
  ([table text-fn icon]
   (let [label (label/create "")
         sub-table (table/create
                    {:rows [[{:actor (image/create {:image/object icon})}
                             label]]})]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table sub-table)))))
  ([table text-fn]
   (let [label (label/create "")]
     (group/add-actor! table (set-label-text-actor label text-fn))
     (cell/expand-x! (cell/right! (table/add! table label))))))

(defn- create-window [label items]
  (window/create
   {:pack? true
    :title label
    :rows [(for [{:keys [label on-click]} items]
             {:actor (doto (text-button/create label ui/skin)
                       (actor/add-listener!
                        (change-listener/create
                         (fn [event actor]
                           (on-click actor (stage/ctx (event/stage event)))))))})]}))

(defn- main-table [menus update-labels]
  (let [table (table/create
               {:rows [(for [{:keys [label items]} menus]
                         {:actor (doto (text-button/create label ui/skin)
                                   (actor/add-listener!
                                    (change-listener/create
                                     (fn [event actor]
                                       (.addActor (event/stage event) (create-window label items))))))})]})]
    (doseq [{:keys [label update-fn icon]} update-labels]
      (let [update-fn #(str label ": " (update-fn %))]
        (if icon
          (add-upd-label! table update-fn icon)
          (add-upd-label! table update-fn))))
    table))

(defmethod stage/build :actor/dev-menu
  [{:keys [menus update-labels]}]
  (table/create
   {:rows [[{:actor (main-table menus update-labels)
             :expand-x? true
             :fill-x? true
             :colspan 1}]
           [{:actor (doto (vis-label/create "")
                      (actor/set-touchable! touchable/disabled))
             :expand? true
             :fill-x? true
             :fill-y? true}]]
    :fill-parent? true}))
