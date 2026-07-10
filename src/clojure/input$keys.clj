(ns clojure.input$keys
  (:require [com.badlogic.gdx.input$keys :as input-keys]))

(defn key-to-value [k]
  (case k
    :input.keys/d input-keys/D
    :input.keys/a input-keys/A
    :input.keys/w input-keys/W
    :input.keys/s input-keys/S
    :input.keys/minus input-keys/MINUS
    :input.keys/equals input-keys/EQUALS
    :input.keys/p input-keys/P
    :input.keys/space input-keys/SPACE
    :input.keys/escape input-keys/ESCAPE
    :input.keys/i input-keys/I
    :input.keys/e input-keys/E
    :input.keys/enter input-keys/ENTER
    :input.keys/left input-keys/LEFT
    :input.keys/right input-keys/RIGHT
    :input.keys/up input-keys/UP
    :input.keys/down input-keys/DOWN))
