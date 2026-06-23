(ns editor.map-widget-table.get-value
  (:require [gdl.get-user-object :refer [get-user-object]]
            [scene2d.group.children :refer [children]]
            [moon.schema.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object) (children table))
              :let [[k _] (get-user-object widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
