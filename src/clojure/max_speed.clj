(ns clojure.max-speed
  (:require [clojure.max-delta :refer [max-delta]]
            [clojure.minimum-size :refer [minimum-size]]))

(defn step [_ctx]
  (/ minimum-size max-delta))
