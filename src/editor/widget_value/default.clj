(ns editor.widget-value.default
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]))

(defn f
  [_  widget _schemas]
  ((get-user-object widget) 1))
