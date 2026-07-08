(ns clojure.moon.create-context
  (:require [clojure.context :as context]))

(defn f [ctx]
  (merge (context/map->R {}) ctx))
