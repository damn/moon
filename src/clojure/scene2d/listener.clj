(ns clojure.scene2d.listener)

(defmulti create
  (fn [[listener-k listener-params]]
    listener-k))
