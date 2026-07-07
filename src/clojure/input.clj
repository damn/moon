(ns clojure.input
  (:import (com.badlogic.gdx Input)))

(defn get-x [input]
  (Input/.getX input))

(defn get-y [input]
  (Input/.getY input))

(defn button-just-pressed? [input button-code]
  (Input/.isButtonJustPressed input button-code))

(defn key-just-pressed? [input key-code]
  (Input/.isKeyJustPressed input key-code))

(defn key-pressed? [input key-code]
  (Input/.isKeyPressed input key-code))

(defn set-input-processor! [input processor]
  (Input/.setInputProcessor input processor))
