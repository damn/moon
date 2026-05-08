(ns moon.ui.group)

(defprotocol Group
  (add-actor! [_ actor])
  (children [_])
  (find-actor [_ name])
  (clear-children! [_])
  (set-opts! [_ opts]))
