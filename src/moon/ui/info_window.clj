(ns moon.ui.info-window
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [gdl.ui.label :as label]
            [gdl.ui.stage :as stage]
            [moon.ui.widget-group :as widget-group]
            [moon.ui.window :as window]))

(defn create
  [skin
   {:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (label/create "" skin)
        window (window/create {:skin skin
                               :title title
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
