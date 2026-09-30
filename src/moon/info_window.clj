(ns moon.info-window
  (:require [gdx.actor.group.widget.table :as table]
            [gdx.actor.group :as group]
            [gdx.actor.widget.label :as label]
            [gdx.actor.group.widget.table.window :as window]
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
                 (.setName actor-name)
                 (.setVisible visible?))]
    (let [[x y] position]
      (.setPosition ^com.badlogic.gdx.scenes.scene2d.Actor window (float x) (float y)))
    (group/add-actor! window (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
                               (act [delta]
                                 (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                                   (label/set-text! label (set-label-text! (:stage/ctx stage))))
                                 (layout/pack window)
                                 (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                                   (proxy-super act delta)))
                               (draw [batch parent-alpha])))
    window))
