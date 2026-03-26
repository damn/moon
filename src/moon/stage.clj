(ns moon.stage)

(defprotocol Stage
  (add-actor! [_ actor])
  (mouseover-actor [_ [x y]]))
