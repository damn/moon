(ns gdx.scene2d.ui.info-window
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [gdx.scene2d.actor.set-position :refer [set-position!]]
            [gdx.scene2d.actor :as actor]
            [gdx.scene2d.ui.label :as label]
            [gdx.scene2d.ui.table :as table]
            [gdx.scene2d.ui.window :as window]))

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
        window (doto (window/create
                      {:title title
                       :skin skin
                       :table/rows [[{:actor label :expand? true}]]})
                 (gdx-actor/set-name! actor-name)
                 (gdx-actor/set-visible! visible?)
                 (set-position! position))]
    (group/add-actor! window (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (gdx-actor/get-stage this)]
                                    (gdx-label/set-text! label (set-label-text! (:stage/ctx stage))))
                                  (layout/pack! window))}))
    window))
