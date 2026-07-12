(ns moon.info-window
  (:require [moon.table :as moon-table]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as gdx-label]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn create
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (label/new "MY LABEL TEXT" skin)
        window (doto (doto (window/new title skin)
                      (moon-table/set-opts!
                       {:table/rows [[{:actor label :expand? true}]]}))
                 (actor/set-name! actor-name)
                 (actor/set-visible! visible?))]
    (let [[x y] position]
      (actor/set-position! window x y))
    (group/addActor window (actor/new
                            (fn [this _delta]
                              (when-let [stage (actor/get-stage this)]
                                (gdx-label/setText label (set-label-text! (:stage/ctx stage))))
                              (layout/pack window))
                            (fn [_actor _batch _parent-alpha])))
    window))
