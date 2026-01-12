(ns moon.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener)
           (moon Stage)))

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
          (on-clicked actor (.ctx ^Stage (Event/.getStage event)))))))))
