(ns moon.ui.impl.text-button
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defmethod actor/create :ui/text-button
  [{:keys [text skin] :as opts}]
  (doto (TextButton. ^String text ^Skin skin)
    (actor/set-opts! opts)))
