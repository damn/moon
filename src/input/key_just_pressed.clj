(ns input.key-just-pressed
  (:require [gdx.input.keys :as input.keys])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyJustPressed ^Input input (input.keys/k->value k)))
