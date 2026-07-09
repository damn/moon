(ns clojure.srand
  (:require [clojure.java.util.random :as random]))

(defn srand [& args]
  (apply random/srand args))
