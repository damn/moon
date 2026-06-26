(ns editor.widget-value.string
  (:require [scene2d.ui.text-field.get-text :as get-text]))

(defn f
  [_ widget _schemas]
  (get-text/f widget))
