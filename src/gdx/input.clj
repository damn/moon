(ns gdx.input
  (:import (com.badlogic.gdx Input
                             Input$Buttons
                             Input$Keys
                             InputProcessor)))

(defn- key->code [k]
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

(defn- button->code [k]
  (case k
    :input.buttons/left Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT))

(defn position [input]
  [(.getX ^Input input)
   (.getY ^Input input)])

(defn key-pressed? [input key]
  (.isKeyPressed ^Input input (key->code key)))

(defn key-just-pressed? [input key]
  (.isKeyJustPressed ^Input input (key->code key)))

(defn button-just-pressed? [input button]
  (.isButtonJustPressed ^Input input (button->code button)))

(defn set-processor! [input processor]
  (.setInputProcessor ^Input input ^InputProcessor processor))
