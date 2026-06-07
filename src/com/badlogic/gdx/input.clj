(ns com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input
                             Input$Buttons
                             Input$Keys)))

(defn- k->inputkey [k]
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

(defn set-processor! [^Input input processor]
  (.setInputProcessor input processor))

(defn key-pressed? [^Input input k]
  (.isKeyPressed input (k->inputkey k)))

(defn key-just-pressed? [^Input input k]
  (.isKeyJustPressed input (k->inputkey k)))

(defn position [^Input input]
  [(.getX input)
   (.getY input)])

(defn button-just-pressed? [^Input input k]
  (.isButtonJustPressed input (case k
                                :input.buttons/left  Input$Buttons/LEFT
                                :input.buttons/right Input$Buttons/RIGHT)))
