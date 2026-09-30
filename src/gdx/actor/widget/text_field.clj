(ns gdx.actor.widget.text-field
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextField)))

(defn create [text skin]
  (TextField. ^String text ^Skin skin))

(defn get-text [text-field]
  (.getText ^TextField text-field))
