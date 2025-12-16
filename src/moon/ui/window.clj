(ns moon.ui.window
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.window :as window]
            [gdl.ui.change-listener :as change-listener]
            [moon.ui :as ui]
            [moon.ui.stage :as stage]
            [moon.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextButton
                                               Window)))

; FIXME opts not there anymore
; TODO cannot close !
; TODO WASD in textfield -> player moves -> InputMultiplexer?
(defn- create*
  [{:keys [title
           close-button?
           center?
           close-on-escape?]}]
  (let [#_show-window-border? #_true
        window (Window. title ui/skin)]
    #_(when close-button?    (.addCloseButton window))
    #_(when center?          (.centerWindow   window))
    #_(when close-on-escape? (.closeOnEscape  window))
    window))

(defn create
  [{:keys [modal?] :as opts}]
  (let [window (create* opts)]
    (table/add! (.getTitleTable window)
                (doto (TextButton. "X" ui/skin)
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
