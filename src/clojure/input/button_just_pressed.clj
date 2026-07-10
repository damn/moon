(ns clojure.input.button-just-pressed
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.buttons :as input-buttons]))

(defn f [input k]
  (input/isButtonJustPressed input (input-buttons/key-to-value k)))
