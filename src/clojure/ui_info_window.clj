(ns clojure.ui-info-window
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [clojure.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [clojure.pack! :as pack!]
            [clojure.scene2d.actor.set-position! :refer [set-position!]]
            [clojure.scene2d-actor :as scene2d-actor]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]
            [clojure.ui-window :as window]))

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
                 (actor/setName actor-name)
                 (actor/setVisible visible?)
                 (set-position! position))]
    (group/add-actor! window (scene2d-actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (actor/getStage this)]
                                    (gdx-label/setText label (set-label-text! (:stage/ctx stage))))
                                  (pack!/f window))}))
    window))
