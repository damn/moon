(ns gdx.scenes.scene2d.ui.image-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (image-button/create drawable)
    (actor/set-opts! opts)))
