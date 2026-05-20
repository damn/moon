(ns gdl.application-listener)

(defprotocol ApplicationListener
  (create! [_ app])
  (dispose! [_])
  (render! [_])
  (resize! [_ width height])
  (pause! [_])
  (resume! [_]))
