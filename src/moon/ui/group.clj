(ns moon.ui.group)

(defprotocol Group
  (find-actor [_ actor-name])
  (set-opts! [_ opts]))
