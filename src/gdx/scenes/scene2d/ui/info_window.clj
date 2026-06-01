(ns gdx.scenes.scene2d.ui.info-window
  (:require [gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scene2d.actor.stage :refer [actor-stage]]
            [gdx.scenes.scene2d.group :as group]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.scenes.scene2d.ui.widget-group :as widget-group]))

(defn create
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (label/create
               {:text "MY LABEL TEXT"
                :skin skin})
        window (window/create
                {:title title
                 :skin skin
                 :table/rows [[{:actor label :expand? true}]]
                 :actor/name actor-name
                 :actor/visible? visible?
                 :actor/position position})]
    (group/add-actor! window (actor/create
                              {:act! (fn [this delta]
                                       (when-let [stage (actor-stage this)]
                                         (label/set-text! label (set-label-text! (:stage/ctx stage))))
                                       (widget-group/pack! window))}))
    window))
