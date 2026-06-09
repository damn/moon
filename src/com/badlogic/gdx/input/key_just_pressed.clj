(ns com.badlogic.gdx.input.key-just-pressed
  (:require [com.badlogic.gdx.input.keys :as input.keys])
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input k]
  (.isKeyJustPressed input (input.keys/k->value k)))
