(ns gdl.input
  (:require [clj.api.com.badlogic.gdx.input :as input]
            [clj.api.com.badlogic.gdx.input.buttons :as input.buttons]
            [clj.api.com.badlogic.gdx.input.keys :as input.keys]))

(defn- k->value [k]
  (case k
    :input.keys/enter input.keys/enter
    :input.keys/minus input.keys/minus
    :input.keys/equals input.keys/equals
    :input.keys/p input.keys/p
    :input.keys/space input.keys/space
    :input.keys/escape input.keys/escape
    :input.keys/i input.keys/i
    :input.keys/e input.keys/e
    :input.keys/d input.keys/d
    :input.keys/a input.keys/a
    :input.keys/w input.keys/w
    :input.keys/s input.keys/s
    ))

(defn- b->value [b]
  (case b
    :input.buttons/left input.buttons/left
    :input.buttons/right input.buttons/right
    ))

(defn key-pressed? [input key]
  (input/key-pressed? input (k->value key)))

(defn key-just-pressed? [input key]
  (input/key-just-pressed? input (k->value key)))

(defn button-just-pressed? [input button]
  (input/button-just-pressed? input (b->value button)))

(defn mouse-position [input]
  [(input/x input)
   (input/y input)])

(defn set-processor! [input input-processor]
  (input/set-processor! input input-processor))
