(ns com.badlogic.gdx.input
  (:require [com.badlogic.gdx.input.keys :as input.keys]
            [com.badlogic.gdx.input.buttons :as input.buttons])
  (:import (com.badlogic.gdx Input)))

(defn set-processor! [^Input input processor]
  (.setInputProcessor input processor))

(defn key-pressed? [^Input input k]
  (.isKeyPressed input (input.keys/k->value k)))

(defn key-just-pressed? [^Input input k]
  (.isKeyJustPressed input (input.keys/k->value k)))

(defn position [^Input input]
  [(.getX input)
   (.getY input)])

(defn button-just-pressed? [^Input input k]
  (.isButtonJustPressed input (input.buttons/k->value k)))
