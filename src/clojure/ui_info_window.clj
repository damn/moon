(ns clojure.ui-info-window
  (:require [clojure.group :as group]
            [clojure.actor :as gdx-actor]
            [clojure.label :as gdx-label]
            [clojure.layout :as layout]
            [clojure.actor-set-position :refer [set-position!]]
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
                 (gdx-actor/set-name! actor-name)
                 (gdx-actor/set-visible! visible?)
                 (set-position! position))]
    (group/add-actor! window (actor/f
                         {:act! (fn [this delta]
                                  (when-let [stage (gdx-actor/get-stage this)]
                                    (gdx-label/set-text! label (set-label-text! (:stage/ctx stage))))
                                  (layout/pack! window))}))
    window))
