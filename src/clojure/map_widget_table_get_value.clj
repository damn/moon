(ns clojure.map-widget-table-get-value
  (:require
            [clojure.get-user-object] [clojure.group :as group]
            [clojure.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? clojure.get-user-object/f) (group/get-children table))
              :let [[k _] (clojure.get-user-object/f widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
