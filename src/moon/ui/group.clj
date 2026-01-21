(ns moon.ui.group)

(defprotocol Group
  (children [_])
  (find-actor [_ actor-name])
  (set-opts! [_ opts]))
