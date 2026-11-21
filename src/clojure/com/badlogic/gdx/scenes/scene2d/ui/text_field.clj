(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.text-field
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextField)))

(defn create [^String text ^Skin skin]
  (TextField. text skin))

(defn text [^TextField text-field]
  (.getText text-field))
