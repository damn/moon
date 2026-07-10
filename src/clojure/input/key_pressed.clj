(ns clojure.input.key-pressed
  (:require [gdl.input :as input]
            [gdl.input-keys :as input-keys]))

(defn f [input k]
  (input/key-pressed? input (input-keys/key-to-value k)))
