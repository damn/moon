(ns clojure.sshuffle
  (:require [clojure.java.util.random :as random]))

(defn sshuffle [& args]
  (apply random/sshuffle args))
