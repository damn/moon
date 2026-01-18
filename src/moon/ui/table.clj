(ns moon.ui.table)

(defprotocol Table
  (add-rows! [_ rows])
  (set-opts! [_ opts]))
