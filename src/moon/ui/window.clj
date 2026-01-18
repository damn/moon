(ns moon.ui.window
  (:require [moon.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

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
  [{:keys [^Skin skin modal?] :as opts}]
  (let [window (create* opts skin)]
    (.add (.getTitleTable window)
          ^Actor (doto (TextButton. "X" skin)
                   (.addListener
                    (proxy [ChangeListener] []
                      (changed [_event _actor]
                        (.remove window))))))
    (.setModal window (boolean modal?))
    (table/set-opts! window opts)))
