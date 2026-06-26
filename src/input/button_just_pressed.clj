(ns input.button-just-pressed
  (:require [com.badlogic.gdx.input :as input]
            [gdx.input.buttons :as input.buttons]))

(defn f [input k]
  (input/button-just-pressed? input (input.buttons/k->value k)))
