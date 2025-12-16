(ns moon.ui.info-window
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.group :as group]
            [gdl.ui.label :as label]
            [gdl.ui.widget-group :as widget-group]
            [moon.ui.label :as vis-label]
            [moon.ui.stage :as stage]
            [moon.ui.window :as window]))

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
