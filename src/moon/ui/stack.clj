(ns moon.ui.stack
  (:require [moon.scene2d.ui.stack :as stack]
            [moon.ui.widget-group :as widget-group]))

(defn create [opts]
  (doto (stack/create)
    (widget-group/set-opts! opts)))
