(ns moon.effect.is-applicable)

(defmulti f
  (fn [[k _v] _effect-ctx]
    k))
