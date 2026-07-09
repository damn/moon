(ns clojure.mouse-position
  (:require [clojure.input.position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
