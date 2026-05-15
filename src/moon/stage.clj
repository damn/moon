(ns moon.stage)

; TODO viewport getter
; etc. ..

(defprotocol Stage
  (ctx [_])
  (set-ctx! [_ ctx]) ; with act/draw?
  (add-actor! [_ actor])
  (act! [_])
  (draw! [_])
  (find-actor [_ name])
  (mouseover-actor [_ position])
  (viewport-width [_])
  (viewport-height [_])
  (update-viewport! [_ width height])
  (unproject [_ [x y]]))
