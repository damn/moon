(ns clojure.gdx.scene2d.ui.text-field
  (:require [moon.ui.actor :as actor]
            [moon.ui.text-field :as text-field])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defmethod actor/create :ui/text-field
  [{:keys [text skin] :as opts}]
  (doto (TextField. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type TextField
  text-field/TextField
  (text [text-field]
    (.getText text-field)))
