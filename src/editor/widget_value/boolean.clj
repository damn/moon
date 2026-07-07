(ns editor.widget-value.boolean
  (:require [clojure.checkbox :as checkbox]))

(defn f
  [_ widget _schemas]
  (checkbox/checked? widget))
