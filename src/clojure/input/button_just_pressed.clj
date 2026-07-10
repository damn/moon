(ns clojure.input.button-just-pressed
  (:require [gdl.input :as input]
            [gdl.input.buttons :as input-buttons]))

(defn f [input k]
  (input/button-just-pressed? input (input-buttons/key-to-value k)))
