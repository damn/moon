(ns clojure.vis-ui.check-box
  (:import (com.kotcrab.vis.ui.widget VisCheckBox)))

(def checked? VisCheckBox/.isChecked)

(defn create [text]
  (VisCheckBox. (str text)))
