(ns moon.ui.image
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [moon.ui.actor :as actor]))

(defn create
  [{:keys [content] :as opts}]
  (doto (image/create content)
    (actor/set-opts! opts)))
