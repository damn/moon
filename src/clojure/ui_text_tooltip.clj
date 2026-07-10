(ns clojure.ui-text-tooltip
  (:require [gdl.text-tooltip :as text-tooltip]))

(defn create [tooltip skin]
  (text-tooltip/new tooltip skin))
