(ns gdx.scenes.scene2d.ui.info-window
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.actor.set-visible :as set-visible]
            [clojure.gdx.layout.pack :as pack]
            [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.actor :as actor]
            [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Group)
           (com.badlogic.gdx.scenes.scene2d.ui Label)))

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
                 (set-name/f actor-name)
                 (set-visible/f visible?)
                 (set-position! position))]
    (Group/.addActor window (actor/f
                             {:act! (fn [this delta]
                                      (when-let [stage (get-stage/f this)]
                                        (Label/.setText label ^String (set-label-text! (:stage/ctx stage))))
                                      (pack/f window))}))
    window))
