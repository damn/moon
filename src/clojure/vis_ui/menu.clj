(ns clojure.vis-ui.menu
  (:import (com.kotcrab.vis.ui.widget Menu)))

(defn create [label]
  (Menu. label))
