(ns moon.ui.window
  (:require [moon.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create
  [{:keys [title
           ^Skin skin]
    :as opts}]
  (let [window (Window. (str title) skin)]
    (.add (.getTitleTable window)
          ^Actor (doto (TextButton. "X" skin)
                   (.addListener
                    (proxy [ChangeListener] []
                      (changed [_event _actor]
                        (Actor/.remove window)))))) ; TODO this will permanently remove ui and entity info window?
    (table/set-opts! window opts)))
