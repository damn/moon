(ns clojure.gdx.input
  (:require [clojure.gdx.input.buttons :as buttons]
            [clojure.gdx.input.keys :as keys]
            clojure.input)
  (:import (com.badlogic.gdx Input)))

(extend-type Input
  clojure.input/Input
  (key-pressed? [this key]
    (.isKeyPressed this (keys/k->value key)))

  (key-just-pressed? [this key]
    (.isKeyJustPressed this (keys/k->value key)))

  (button-just-pressed? [this button]
    (.isButtonJustPressed this (buttons/k->value button)))

  (mouse-position [this]
    [(.getX this)
     (.getY this)])

  (set-processor! [this input-processor]
    (.setInputProcessor this input-processor)))

