(ns clojure.vis-ui.popup-menu
  (:import (com.kotcrab.vis.ui.widget PopupMenu)))

(defn add-item! [menu menu-item]
  (PopupMenu/.addItem menu menu-item))
