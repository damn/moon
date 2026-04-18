(ns clojure.gdx.input
  (:require [clojure.gdx.input.buttons :as buttons]
            [clojure.gdx.input.keys :as keys])
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input (keys/k->value key)))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input (keys/k->value key)))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input (buttons/k->value button)))

(defn mouse-position [^Input input]
  [(.getX input)
   (.getY input)])

(defn set-processor! [^Input input input-processor]
  (.setInputProcessor input input-processor))
