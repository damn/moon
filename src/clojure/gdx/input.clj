(ns clojure.gdx.input
  (:require [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input$buttons :as buttons]
            [com.badlogic.gdx.input$keys :as keys]))

(defn- key->code [k]
  (case k
    :input.keys/d keys/D
    :input.keys/a keys/A
    :input.keys/w keys/W
    :input.keys/s keys/S
    :input.keys/minus keys/MINUS
    :input.keys/equals keys/EQUALS
    :input.keys/p keys/P
    :input.keys/space keys/SPACE
    :input.keys/escape keys/ESCAPE
    :input.keys/i keys/I
    :input.keys/e keys/E
    :input.keys/enter keys/ENTER
    :input.keys/left keys/LEFT
    :input.keys/right keys/RIGHT
    :input.keys/up keys/UP
    :input.keys/down keys/DOWN))

(defn- button->code [k]
  (case k
    :input.buttons/left buttons/LEFT
    :input.buttons/right buttons/RIGHT))

(defn position [input]
  [(input/getX input)
   (input/getY input)])

(defn key-pressed? [input key]
  (input/isKeyPressed input (key->code key)))

(defn key-just-pressed? [input key]
  (input/isKeyJustPressed input (key->code key)))

(defn button-just-pressed? [input button]
  (input/isButtonJustPressed input (button->code button)))

(defn set-processor! [input processor]
  (input/setInputProcessor input processor))
