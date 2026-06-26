(ns input.key-pressed
  (:require [com.badlogic.gdx.input :as input]
            [gdx.input.keys :as input.keys]))

(defn f [input k]
  (input/key-pressed? input (input.keys/k->value k)))
