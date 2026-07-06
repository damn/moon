(ns gdx.scenes.scene2d.ui.info-window
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.add-actor :as add-actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.actor :as actor]
            [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]))

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
                 (actor/set-name! actor-name)
                 (actor/set-visible! visible?)
                 (set-position! position))]
    (add-actor/f window (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (actor/get-stage this)]
                                    (label/set-text label (set-label-text! (:stage/ctx stage))))
                                  (layout/pack window))}))
    window))
