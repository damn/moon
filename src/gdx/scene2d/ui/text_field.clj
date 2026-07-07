(ns gdx.scene2d.ui.text-field
  (:require [clojure.text-field :as text-field]))

(defn create [text skin]
  (text-field/new text skin))
