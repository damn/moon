(ns clojure.vis-ui.label
  (:import (com.kotcrab.vis.ui.widget VisLabel)))

(defn create [text]
  (VisLabel. ^CharSequence (str text)))
