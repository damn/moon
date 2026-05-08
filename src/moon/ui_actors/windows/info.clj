(ns moon.ui-actors.windows.info
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.stage :as stage]
            [moon.ui.label :as label]
            [moon.ui.table :as table]
            [clojure.gdx.scene2d.ui.widget-group :as widget-group]))

(defmethod actor/create :ui/info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (actor/create
               {:type :ui/label
                :text "MY LABEL TEXT"
                :skin skin})
        window (actor/create
                {:type :ui/window
                 :title title
                 :skin skin
                 :table/rows [[{:actor label :expand? true}]]
                 :actor/name actor-name
                 :actor/visible? visible?
                 :actor/position position})]
    (group/add-actor! window (actor/create
                              {:type :ui/actor
                               :act! (fn [this delta]
                                       (when-let [stage (actor/stage this)]
                                         (label/set-text! label (set-label-text! (stage/ctx stage))))
                                       (widget-group/pack! window))}))
    window))
