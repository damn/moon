(ns clojure.number.is-nearly-equal
  (:require [clojure.number.float-rounding-error :as float-rounding-error]))

(defn f
  ([x y]
   (f x y float-rounding-error/v))
  ([a b epsilon]
   (<= (Math/abs (- a b))
       epsilon)))


