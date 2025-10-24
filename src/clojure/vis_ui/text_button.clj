(ns clojure.vis-ui.text-button
  (:import (com.kotcrab.vis.ui.widget VisTextButton)))

(defn create [text]
  (VisTextButton. (str text)))
