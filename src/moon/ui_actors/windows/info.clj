(ns moon.ui-actors.windows.info
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.info :as info]
            [moon.stage :as stage]
            [moon.table :as table]))

(defn- create*
  [skin
   {:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (label/create "" skin)
        window (doto (window/create title skin)
                 (table/add-rows! [[{:actor label :expand? true}]])
                 (actor/set-name! actor-name)
                 (actor/set-visible! visible?)
                 (actor/set-position! position))]
    (group/add-actor! window (gdx-actor/create
                              {:act! (fn [this delta]
                                       (when-let [stage (actor/stage this)]
                                         (label/set-text! label (set-label-text! (stage/ctx stage))))
                                       (widget-group/pack! window))}))
    window))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (create* skin
           {:title "Entity Info"
            :actor-name "moon.ui.windows.entity-info"
            :visible? false
            :position [(viewport/world-width (stage/viewport stage)) 0]
            :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                   :as ctx}]
                               (if-let [eid mouseover-eid]
                                 (info/text (apply dissoc @eid [:entity/skills
                                                                :entity/faction
                                                                :active-skill])
                                            ctx)
                                 ""))}))
