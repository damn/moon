(ns moon.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton
                                               Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn add-close-button! [^Window window ^Skin skin]
  (.add (.getTitleTable window) (doto (TextButton. "X" skin)
                                  (.addListener
                                   (proxy [ChangeListener] []
                                     (changed [_event _actor]
                                       (.remove window))))))
  window)
