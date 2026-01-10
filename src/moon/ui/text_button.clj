(ns moon.ui.text-button
  (:require [moon.ui.actor :as actor]
            [moon.ui.change-listener :as change-listener]
            [moon.ui.event :as event]
            [moon.ui.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create
  ([text skin]
   (create {:text text
            :skin skin
            :on-clicked (fn [actor ctx])}))
  ([{:keys [text
            on-clicked
            ^Skin skin]}]
   (doto (TextButton. (str text) skin)
     (actor/add-listener!
      (change-listener/create
       (fn [event actor]
         (on-clicked actor (stage/ctx (event/stage event)))))))))
