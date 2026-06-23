(ns gdx.input-keys
  (:import (com.badlogic.gdx Input$Keys)))

(defn k->value [k]
  (case k
    :input.keys/d Input$Keys/D
    :input.keys/a Input$Keys/A
    :input.keys/w Input$Keys/W
    :input.keys/s Input$Keys/S
    :input.keys/minus Input$Keys/MINUS
    :input.keys/equals Input$Keys/EQUALS
    :input.keys/p Input$Keys/P
    :input.keys/space Input$Keys/SPACE
    :input.keys/escape Input$Keys/ESCAPE
    :input.keys/i Input$Keys/I
    :input.keys/e Input$Keys/E
    :input.keys/enter Input$Keys/ENTER
    :input.keys/left Input$Keys/LEFT
    :input.keys/right Input$Keys/RIGHT
    :input.keys/up Input$Keys/UP
    :input.keys/down Input$Keys/DOWN))
