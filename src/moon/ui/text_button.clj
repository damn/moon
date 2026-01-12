(ns moon.ui.text-button
  (:require [moon.ui.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)))

(defn create
  ([text skin]
   (create {:text text
            :skin skin
            :on-clicked (fn [actor ctx])}))
  ([{:keys [text
            on-clicked
            ^Skin skin]}]
   (doto (TextButton. (str text) skin)
     (.addListener
      (proxy [ChangeListener] []
        (changed [event actor]
          (on-clicked actor (stage/ctx (Event/.getStage event)))))))))
