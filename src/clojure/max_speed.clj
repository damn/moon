(ns clojure.max-speed
  (:require [clojure.max-delta :as max-delta]
            [clojure.minimum-size :as minimum-size]))

(defn step [_ctx]
  (/ minimum-size/minimum-size max-delta/max-delta))
