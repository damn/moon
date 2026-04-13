(ns moon.ui.image-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [moon.ui.actor :as actor]))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (image-button/create drawable)
    (actor/set-opts! opts)))
