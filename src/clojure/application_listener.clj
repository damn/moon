(ns clojure.application-listener)

(defprotocol ApplicationListener
  (create! [_ application])
  (dispose! [_])
  (render! [_])
  (resize! [_ width height])
  (pause! [_])
  (resume! [_]))
