(ns clojure.moon.create-context
  (:require [clojure.moon.context :as context]))

(defn f [ctx]
  (merge (context/map->R {}) ctx))
