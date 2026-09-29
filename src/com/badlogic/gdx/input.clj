(ns com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input InputProcessor)))

(defn getX [input]
  (.getX ^Input input))

(defn getY [input]
  (.getY ^Input input))

(defn isButtonJustPressed [input button-code]
  (.isButtonJustPressed ^Input input button-code))

(defn isKeyJustPressed [input key-code]
  (.isKeyJustPressed ^Input input key-code))

(defn isKeyPressed [input key-code]
  (.isKeyPressed ^Input input key-code))

(defn setInputProcessor [input processor]
  (.setInputProcessor ^Input input ^InputProcessor processor))
