(ns moon.stage)

(defprotocol Stage
  (ctx [_])
  (add-actor! [_ actor])
  (mouseover-actor [_ [x y]])
  (viewport [_]))
