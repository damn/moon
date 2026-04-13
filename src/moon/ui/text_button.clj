(ns moon.ui.text-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [moon.ui.actor :as actor]))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (text-button/create text skin)
    (actor/set-opts! opts)))
