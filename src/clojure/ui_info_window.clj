(ns clojure.ui-info-window
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [clojure.table-set-opts :as table-set-opts]))

(defn create
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (label/new "MY LABEL TEXT" skin)
        window (doto (doto (window/new title skin)
                      (table-set-opts/set-opts!
                       {:table/rows [[{:actor label :expand? true}]]}))
                 (actor/setName actor-name)
                 (actor/setVisible visible?))]
    (let [[x y] position]
      (actor/setPosition window x y))
    (group/addActor window (actor/new
                            (fn [this _delta]
                              (when-let [stage (actor/getStage this)]
                                (gdx-label/setText label (set-label-text! (:stage/ctx stage))))
                              (layout/pack window))
                            (fn [_actor _batch _parent-alpha])))
    window))
