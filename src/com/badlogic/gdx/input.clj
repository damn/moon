(ns com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input)))

(defn get-x [input]
  (Input/.getX input))

(defn get-y [input]
  (Input/.getY input))

(defn is-button-just-pressed [input button-code]
  (Input/.isButtonJustPressed input button-code))

(defn is-key-just-pressed [input key-code]
  (Input/.isKeyJustPressed input key-code))

(defn is-key-pressed [input key-code]
  (Input/.isKeyPressed input key-code))

(defn set-input-processor! [input processor]
  (Input/.setInputProcessor input processor))
