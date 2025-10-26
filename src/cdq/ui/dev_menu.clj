(ns cdq.ui.dev-menu
  (:require [cdq.ui.image :as image]
            [cdq.ui.stage :as stage]
            [cdq.ui.table :as table]
            [cdq.ui.window :as window]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.event :as event]
            [moon.scene2d.group :as group]
            [moon.scene2d.touchable :as touchable]
            [moon.scene2d.ui.cell :as cell]
            [moon.scene2d.utils.change-listener :as change-listener]
            [moon.ui.label :as label]
            [moon.ui.text-button :as text-button]
            [moon.ui.label :as vis-label]))

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
             {:actor (doto (text-button/create label)
                       (actor/add-listener!
                        (change-listener/create
                         (fn [event actor]
                           (on-click actor (stage/ctx (event/stage event)))))))})]}))

(defn- main-table [menus update-labels]
  (let [table (table/create
               {:rows [(for [{:keys [label items]} menus]
                         {:actor (doto (text-button/create label)
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
