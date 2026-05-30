(ns gdx.scenes.scene2d.ui.image
  (:require [gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]))

(defn create
  [{:keys [content] :as opts}]
  (doto (image/create content)
    (actor/set-opts! opts)))

(defn set-drawable! [image drawable]
  (image/set-drawable! image drawable))
