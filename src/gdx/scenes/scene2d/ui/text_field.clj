(ns gdx.scenes.scene2d.ui.text-field
  (:require [clojure.gdx.scene2d.ui.text-field :as text-field]))

(defn create
  [{:keys [text skin]}]
  (text-field/create text skin))

(defn text [text-field]
  (text-field/text text-field))
