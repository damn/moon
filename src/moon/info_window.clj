(ns moon.info-window
  (:require [gdx.scenes.scene2d.ui.table :as table]
            [gdx.actor :as actor]
            [gdx.actor.group :as group]
            [gdx.scenes.scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.layout :as layout]))

(defn create
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [label (label/create "MY LABEL TEXT" skin)
        window (doto (window/create {:title title
                                     :skin skin
                                     :table/rows [[{:actor label :expand? true}]]})
                 (actor/set-name! actor-name)
                 (actor/set-visible! visible?))]
    (let [[x y] position]
      (actor/set-position! window x y))
    (group/add-actor! window (actor/new
                            (fn [this _delta]
                              (when-let [stage (actor/get-stage this)]
                                (label/set-text! label (set-label-text! (:stage/ctx stage))))
                              (layout/pack window))
                            (fn [_actor _batch _parent-alpha])))
    window))
