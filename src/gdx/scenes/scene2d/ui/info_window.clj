(ns gdx.scenes.scene2d.ui.info-window
  (:require [clojure.gdx :as gdx]
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
                 (gdx/set-name! actor-name)
                 (gdx/set-visible! visible?)
                 (set-position! position))]
    (gdx/add-actor! window (actor/f
                            {:act! (fn [this delta]
                                     (when-let [stage (gdx/get-stage this)]
                                       (gdx/label-set-text! label ^String (set-label-text! (:stage/ctx stage))))
                                     (gdx/pack! window))}))
    window))
