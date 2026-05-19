(ns gdl.utils.disposable)

(defprotocol Disposable
  (dispose! [_]))
