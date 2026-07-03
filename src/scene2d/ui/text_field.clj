(ns scene2d.ui.text-field
  (:require [clojure.gdx.text-field.new :as new-text-field]))

(defn create [text skin]
  (new-text-field/f text skin))
