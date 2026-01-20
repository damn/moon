(ns moon.stage)

(defprotocol Stage
  (add-actor! [_ actor])
  (root [_])
  (mouseover-actor [_ [x y]])
  (viewport [_]))
