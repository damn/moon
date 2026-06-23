(ns gdx.scenes.scene2d.ui.info-window
  (:require [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.actor.get-stage :as get-stage]
            [scene2d.actor.set-name :refer [set-name!]]
            [scene2d.actor.set-visible :refer [set-visible!]]
            [scene2d.actor :as actor]
            [scene2d.group.add-actor :refer [add-actor!]]
            [ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.layout.pack :refer [pack!]]))

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
    (add-actor! window (actor/f
                        {:act! (fn [this delta]
                                 (when-let [stage (get-stage/f this)]
                                   (label/set-text! label (set-label-text! (:stage/ctx stage))))
                                 (pack! window))}))
    window))
