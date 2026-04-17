(ns clojure.disposable)

(defprotocol Disposable
  (dispose! [_]))
