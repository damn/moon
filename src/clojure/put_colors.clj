(ns clojure.put-colors
  (:require [clojure.colors :as colors]
            [clojure.color :as color]))

(defn step [_ctx]
  (colors/put! "PRETTY_NAME" (color/new [0.84 0.8 0.52 1])))
