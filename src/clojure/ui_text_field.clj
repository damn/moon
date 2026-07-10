(ns clojure.ui-text-field
  (:require [gdl.text-field :as text-field]))

(defn create [text skin]
  (text-field/new text skin))
