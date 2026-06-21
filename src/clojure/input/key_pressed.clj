(ns clojure.input.key-pressed
  (:require [clojure.input-keys :as input.keys])
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input k]
  (.isKeyPressed input (input.keys/k->value k)))
