(ns moon.window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Window)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn add-close-button! [^Window window skin]
  (.add (.getTitleTable window) (doto ^Actor (text-button/create "X" skin)
                                  (.addListener
                                   (proxy [ChangeListener] []
                                     (changed [_event _actor]
                                       (.remove window))))))
  window)
