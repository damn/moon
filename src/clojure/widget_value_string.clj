(ns clojure.widget-value-string
  (:require [clojure.text-field :as text-field]))

(defn f
  [_ widget _schemas]
  (text-field/get-text widget))
