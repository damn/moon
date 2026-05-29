(ns gdx.scenes.scene2d.ui.label
  (:require [gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(defn set-text! [^Label label text]
  (.setText label ^String text))
