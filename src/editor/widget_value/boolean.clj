(ns editor.widget-value.boolean
  (:require [scene2d.ui.check-box.is-checked :as checked?]))

(defn f
  [_ widget _schemas]
  (checked?/f widget))
