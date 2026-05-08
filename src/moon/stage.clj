(ns moon.stage)

(defprotocol Stage
  (ctx [_])
  (set-ctx! [_ ctx])
  (add-actor! [_ actor])
  (viewport [_])
  (act! [_])
  (draw! [_])
  (find-actor [_ name])
  (mouseover-actor [_ position]))
