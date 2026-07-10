(ns clojure.input.key-just-pressed
  (:require [gdl.input :as input]
            [gdl.input.keys :as input-keys]))

(defn f [input k]
  (input/key-just-pressed? input (input-keys/key-to-value k)))
