(ns clojure.gdx.scene2d.ui.label
  (:require [moon.ui.actor :as actor]
            moon.ui.label)
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmethod actor/create :ui/label
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type Label
  moon.ui.label/Label
  (set-text! [label ^String text]
    (.setText label text)))
