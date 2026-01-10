(ns moon.ui.window
  (:require [moon.ui.actor :as actor]
            [moon.ui.change-listener :as change-listener]
            [moon.ui.text-button :as text-button]
            [moon.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

; FIXME opts not there anymore
; TODO cannot close !
; TODO WASD in textfield -> player moves -> InputMultiplexer?
(defn- create*
  [{:keys [title
           close-button?
           center?
           close-on-escape?]}
   ^Skin skin]
  (let [#_show-window-border? #_true
        window (Window. (str title) skin)]
    #_(when close-button?    (.addCloseButton window))
    #_(when center?          (.centerWindow   window))
    #_(when close-on-escape? (.closeOnEscape  window))
    window))

(defn create
  [{:keys [skin modal?] :as opts}]
  (let [window (create* opts skin)]
    (table/add! (.getTitleTable window)
                (doto (text-button/create "X" skin)
                  (actor/add-listener!
                   (change-listener/create
                    (fn [_event _actor]
                      (actor/remove! window))))))
    (.setModal window (boolean modal?))
    (table/set-opts! window opts)))

(defn find-ancestor
  "Finds the ancestor window of actor, otherwise throws an error if none of recursively searched parents of actors is a window actor."
  [actor]
  (if-let [parent (actor/parent actor)]
    (if (instance? Window parent)
      parent
      (find-ancestor parent))
    (throw (Error. (str "Actor has no parent window " actor)))))
