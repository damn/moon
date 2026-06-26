(ns editor.widget-value.map
  (:require [editor.map-widget-table.get-value :as get-value]))

(defn f
  [_ table schemas]
  (get-value/f table schemas))
