(ns clojure.vis-ui.separator
  (:import (com.kotcrab.vis.ui.widget Separator)))

(defn horizontal []
  (Separator. "default"))

(defn vertical []
  (Separator. "vertical"))
