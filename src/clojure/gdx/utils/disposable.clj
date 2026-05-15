(ns clojure.gdx.utils.disposable)

(defprotocol Disposable
  (dispose! [_]))
