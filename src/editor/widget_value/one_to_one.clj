(ns editor.widget-value.one-to-one
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.group.children :refer [children]]))

(defn f [_  widget _schemas]
  (->> (children widget)
       (keep get-user-object)
       first))
