(ns clojure.editor.map-widget-table.get-value
  (:require [clojure.actor.get-user-object :refer [get-user-object]]
            [clojure.group.children :refer [children]]
            [moon.schema.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object) (children table))
              :let [[k _] (get-user-object widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
