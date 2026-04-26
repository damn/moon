(ns clojure.gdx.scene2d.ui.text-button
  (:require [clojure.gdx.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defmethod actor/create :ui/text-button
  [{:keys [text skin] :as opts}]
  (doto (TextButton. ^String text ^Skin skin)
    (actor/set-opts! opts)))
