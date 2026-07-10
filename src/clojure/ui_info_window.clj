(ns clojure.ui-info-window
  (:require
            [gdl.actor :as actor] [clojure.scene2d.group :as group]
            [gdl.label :as gdx-label]
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
                 (actor/set-name actor-name)
                 (actor/set-visible visible?)
                 (set-position! position))]
    (group/add-actor! window (scene2d-actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (actor/get-stage this)]
                                    (gdx-label/set-text! label (set-label-text! (:stage/ctx stage))))
                                  (pack!/f window))}))
    window))
