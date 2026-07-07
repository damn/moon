(ns clojure.widget-value-number
  (:require [clojure.edn :as edn]
            [clojure.text-field :as text-field]))

(defn f
  [_  widget _schemas]
  (edn/read-string (text-field/get-text widget)))
