(ns gdx.scenes.scene2d.ui.info-window
  (:require [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.actor :as actor]
            [scene2d.ui.label :as label]
            [scene2d.ui.label.set-text :as set-text!]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.utils.layout.pack :refer [pack!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

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
                 (Actor/.setName actor-name)
                 (Actor/.setVisible visible?)
                 (set-position! position))]
    (Group/.addActor window (actor/f
                             {:act! (fn [this delta]
                                      (when-let [stage (Actor/.getStage this)]
                                        (set-text!/f label (set-label-text! (:stage/ctx stage))))
                                      (pack! window))}))
    window))
