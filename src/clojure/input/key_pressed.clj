(ns clojure.input.key-pressed
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]))

(defn f [input k]
  (input/isKeyPressed input (input-keys/key-to-value k)))
