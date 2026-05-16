(ns moon.stage)

(defprotocol Stage
  (set-ctx! [_ ctx])
  (add-actor! [_ actor])
  (act! [_])
  (draw! [_])
  (find-actor [_ name])
  (mouseover-actor [_ position])
  (unproject [_ [x y]]))
