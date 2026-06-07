(ns gdx.scenes.scene2d.ui.info-window
  (:require [com.badlogic.gdx.scenes.scene2d.actor.set-position :refer [set-position!]]
            [com.badlogic.gdx.scenes.scene2d.actor.get-stage :refer [get-stage]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-name :refer [set-name!]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-visible :refer [set-visible!]]
            [com.badlogic.gdx.scenes.scene2d.actor.create :as actor]
            [com.badlogic.gdx.scenes.scene2d.group.add-actor :refer [add-actor!]]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.layout.pack :refer [pack!]]))

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
                 (set-name! actor-name)
                 (set-visible! visible?)
                 (set-position! position))]
    (add-actor! window (actor/create
                        {:act! (fn [this delta]
                                 (when-let [stage (get-stage this)]
                                   (label/set-text! label (set-label-text! (:stage/ctx stage))))
                                 (pack! window))}))
    window))
