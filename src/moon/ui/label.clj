(ns moon.ui.label
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [moon.ui.actor :as actor]))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (label/create text skin)
    (actor/set-opts! opts)))
