(ns cdq.ui.window
  (:require [cdq.ui.table :as table]
            [cdq.ui.stage :as stage]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.ui.window :as window]
            [moon.scene2d.utils.change-listener :as change-listener]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as vis-window])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn create
  [{:keys [modal?] :as opts}]
  (let [window (vis-window/create opts)]
    (table/add! (.getTitleTable window)
                (doto (text-button/create "X")
                  (actor/add-listener!
                   (change-listener/create
                    (fn [_event _actor]
                      (actor/remove! window))))))
    (window/set-modal! window (boolean modal?))
    (table/set-opts! window opts)))

(defmethod stage/build :actor/window [opts]
  (create opts))

(defn find-ancestor
  "Finds the ancestor window of actor, otherwise throws an error if none of recursively searched parents of actors is a window actor."
  [actor]
  (if-let [parent (actor/parent actor)]
    (if (instance? Window parent)
      parent
      (find-ancestor parent))
    (throw (Error. (str "Actor has no parent window " actor)))))
