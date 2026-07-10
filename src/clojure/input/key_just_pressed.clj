(ns clojure.input.key-just-pressed
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]))

(defn f [input k]
  (input/isKeyJustPressed input (input-keys/key-to-value k)))
