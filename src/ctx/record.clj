(ns ctx.record
  (:require [moon.records.context :as context]))

(defn step [ctx]
  (merge (context/map->R {}) ctx))
