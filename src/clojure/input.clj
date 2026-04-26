(ns clojure.input
  (:require [clojure.gdx.input.buttons :as buttons]
            [clojure.gdx.input.keys :as keys])
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [this key]
  (.isKeyPressed this (keys/k->value key)))

(defn key-just-pressed? [this key]
  (.isKeyJustPressed this (keys/k->value key)))

(defn button-just-pressed? [this button]
  (.isButtonJustPressed this (buttons/k->value button)))

(defn mouse-position [this]
  [(.getX this)
   (.getY this)])

(defn set-processor! [this input-processor]
  (.setInputProcessor this input-processor))
