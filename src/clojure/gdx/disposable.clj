(ns clojure.gdx.disposable)

(defprotocol Disposable
  (dispose! [_]))
