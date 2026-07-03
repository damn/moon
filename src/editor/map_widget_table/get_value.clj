(ns editor.map-widget-table.get-value
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.get-children :as get-children]
            [editor.widget-value :as widget-value]))

(defn f [table schemas]
  (into {}
        (for [widget (filter (comp vector? get-user-object/f) (get-children/f table))
              :let [[k _] (get-user-object/f widget)]]
          [k (widget-value/f (get schemas k) widget schemas)])))
