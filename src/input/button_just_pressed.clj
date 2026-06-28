(ns input.button-just-pressed
  (:require [gdx.input.buttons :as input.buttons])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isButtonJustPressed ^Input input (input.buttons/k->value k)))
