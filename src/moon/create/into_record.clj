(ns moon.create.into-record
  (:require [qrecord.core :as q]))

(q/defrecord Context [])

(defn step [ctx]
  (merge (map->Context {}) ctx))
