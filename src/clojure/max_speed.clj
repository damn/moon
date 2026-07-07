(ns clojure.max-speed
  (:require [clojure.max-delta :as max-delta]))

(defn step
  [{:keys [ctx/minimum-size]}]
  (/ minimum-size max-delta/max-delta))
