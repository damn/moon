(ns clojure.scene2d.ui.table)

(defprotocol Table
  (add! [_ cell-declaration])
  (add-rows! [_ rows])
  (set-opts! [_ opts]))
