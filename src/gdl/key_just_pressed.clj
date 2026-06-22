(ns gdl.key-just-pressed
  (:require [gdl.input-keys :as input.keys])
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input k]
  (.isKeyJustPressed input (input.keys/k->value k)))
