(ns clojure.is-useful)

(defmulti f
  (fn [[k _v] _effect-ctx _ctx]
    k))
