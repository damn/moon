(ns clojure.vis-ui.menu-item
  (:import (com.kotcrab.vis.ui.widget MenuItem)))

(defn create [label]
  (MenuItem. label))
