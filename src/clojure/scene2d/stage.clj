(ns clojure.scene2d.stage)

(defprotocol Stage
  (ctx [_])
  (set-ctx! [_ ctx])
  (add-actor! [_ actor])
  (mouseover-actor [_ [x y]])
  (viewport [_])
  (find-actor [_ name])
  (act! [_])
  (draw! [_]))
