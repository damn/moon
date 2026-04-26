(ns clojure.gdx.scene2d.ui.label
  (:require [clojure.gdx.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmethod actor/create :ui/label
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(defn set-text! [^Label label ^String text]
  (.setText label text))
