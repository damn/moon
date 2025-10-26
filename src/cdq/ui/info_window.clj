(ns cdq.ui.info-window
  (:require [cdq.ui.stage :as stage]
            [cdq.ui.window :as window]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.group :as group]
            [moon.scene2d.ui.label :as label]
            [moon.scene2d.ui.widget-group :as widget-group]
            [moon.ui.label :as vis-label]))

(defn create
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (vis-label/create "")
        window (window/create {:title title
                               :actor/name actor-name
                               :actor/visible? visible?
                               :actor/position position
                               :rows [[{:actor label
                                        :expand? true}]]})]
    (group/add-actor! window (actor/create
                              {:act (fn [this _delta]
                                      (when-let [stage (actor/stage this)]
                                        (label/set-text! label (set-label-text! (stage/ctx stage))))
                                      (widget-group/pack! window))
                               :draw (fn [this batch parent-alpha])}))
    window))
