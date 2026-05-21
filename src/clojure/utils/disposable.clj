(ns clojure.utils.disposable)

(defprotocol Disposable
  (dispose! [_]))
