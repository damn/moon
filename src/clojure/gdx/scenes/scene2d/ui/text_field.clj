(ns clojure.gdx.scenes.scene2d.ui.text-field
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]))

(defn create [text skin]
  (text-field/new text skin))

(defn get-text [text-field]
  (text-field/getText text-field))
