(ns moon.stage)

(defprotocol Stage
  (mouseover-actor [_ [x y]]))
