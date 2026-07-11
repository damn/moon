(ns clojure.v
  (:require [clojure.lang.persistent-vector :as v]))

(defn index-of [v k]
  (let [idx (v/indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))
