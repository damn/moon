(ns moon.stage)

(defprotocol Stage
  (ctx [_])
  (add-actor! [_ actor])
  (root [_])
  (mouseover-actor [_ [x y]])
  (viewport [_]))
