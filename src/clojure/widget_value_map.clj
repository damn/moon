(ns clojure.widget-value-map
  (:require [clojure.map-widget-table-get-value :as get-value]))

(defn f
  [_ table schemas]
  (get-value/f table schemas))
