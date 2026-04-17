(ns clojure.scene2d.ui.table)

(defprotocol Table
  (add! [_ cell-declaration])
  (add-rows! [_ rows])
  (set-cell-defaults! [_ cell-opts]) ; TODO in set-opts?
  (set-opts! [_ opts]))
