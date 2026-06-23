(ns input.button-just-pressed
  (:require [gdl.input-buttons :as input.buttons])
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input k]
  (.isButtonJustPressed input (input.buttons/k->value k)))
