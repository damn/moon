(ns moon.stage)

(defprotocol Stage
  (ctx [_])
  (set-ctx! [_ ctx])
  (add-actor! [_ actor])
  (mouseover-actor [_ [x y]])
  (viewport [_]))
