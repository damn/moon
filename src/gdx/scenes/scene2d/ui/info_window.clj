(ns gdx.scenes.scene2d.ui.info-window
  (:require [clojure.gdx.scene2d.actor.create :as actor]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actor!]]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.ui.widget-group.pack :refer [pack!]]))

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
    (add-actor! window (actor/create
                        {:act! (fn [this delta]
                                 (when-let [stage (.getStage this)]
                                   (label/set-text! label (set-label-text! (:stage/ctx stage))))
                                 (pack! window))}))
    window))
