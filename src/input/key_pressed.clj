(ns input.key-pressed
  (:require [gdx.input.keys :as input.keys])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyPressed ^Input input (input.keys/k->value k)))
