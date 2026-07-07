(ns clojure.record
  (:require [clojure.context :as context]))

(defn step [ctx]
  (merge (context/map->R {}) ctx))
