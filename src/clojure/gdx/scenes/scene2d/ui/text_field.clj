(ns clojure.gdx.scenes.scene2d.ui.text-field
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.scene2d.ui.text-field :as text-field])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defmethod actor/create :ui/text-field
  [{:keys [text skin] :as opts}]
  (doto (TextField. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.TextField
  text-field/TextField
  (text [text-field]
    (.getText text-field)))
