(ns clojure.ui-info-window
  (:require
            [clojure.scene2d.actor.get-stage]
            [clojure.scene2d.actor.set-name]
            [clojure.scene2d.actor.set-visible] [clojure.scene2d.group :as group]
            [clojure.label :as gdx-label]
            [clojure.pack! :as pack!]
            [clojure.scene2d.actor.set-position! :refer [set-position!]]
            [clojure.scene2d-actor :as actor]
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
                 (clojure.scene2d.actor.set-name/f actor-name)
                 (clojure.scene2d.actor.set-visible/f visible?)
                 (set-position! position))]
    (group/add-actor! window (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (clojure.scene2d.actor.get-stage/f this)]
                                    (gdx-label/set-text! label (set-label-text! (:stage/ctx stage))))
                                  (pack!/f window))}))
    window))
