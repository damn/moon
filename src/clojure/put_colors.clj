(ns clojure.put-colors
  (:require [clojure.colors :as colors]
            [clojure.new-color]))

(defn step [_ctx]
  (colors/put! "PRETTY_NAME" (clojure.new-color/f [0.84 0.8 0.52 1])))
