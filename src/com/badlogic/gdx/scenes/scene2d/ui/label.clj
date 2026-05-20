(ns com.badlogic.gdx.scenes.scene2d.ui.label
  (:require [gdl.scene2d.actor :as actor]
            [gdl.scene2d.ui.label :as label])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmethod actor/create :ui/label
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type Label
  label/Label
  (set-text! [label text]
    (.setText label ^String text)))

