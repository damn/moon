(ns moon.ui-actors.windows.info
  (:require [gdl.scene2d.group :as group]
            [gdl.scene2d.ui.label :as label]
            [gdl.scene2d.ui.widget-group :as widget-group]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.info :as info]
            [moon.stage :as stage]
            [moon.table :as table]
            [moon.ui :as ui]))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (let [title "Entity Info"
        actor-name "moon.ui.windows.entity-info"
        visible? false
        position [(viewport/world-width (stage/viewport stage)) 0]
        set-label-text! (fn [{:keys [ctx/mouseover-eid]
                              :as ctx}]
                          (if-let [eid mouseover-eid]
                            (info/text (apply dissoc @eid [:entity/skills
                                                           :entity/faction
                                                           :active-skill])
                                       ctx)
                            ""))
        label (ui/create
               {:type :ui/label
                :text "MY LABEL TEXT"
                :skin skin})
        window (ui/create
                {:type :ui/window
                 :title title
                 :skin skin
                 :table/rows [[{:actor label :expand? true}]]
                 :actor/name actor-name
                 :actor/visible? visible?
                 :actor/position position})]
    (group/add-actor! window (ui/create
                              {:type :ui/actor
                               :act! (fn [this delta]
                                       (when-let [stage (actor/stage this)]
                                         (label/set-text! label (set-label-text! (stage/ctx stage))))
                                       (widget-group/pack! window))}))
    window))
