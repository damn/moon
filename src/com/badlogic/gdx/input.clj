(ns com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [^Input input k]
  (.isKeyPressed input k))

(defn key-just-pressed? [^Input input k]
  (.isKeyJustPressed input k))

(defn button-just-pressed? [^Input input k]
  (.isButtonJustPressed input k))

(defn set-input-processor! [^Input input processor]
  (.setInputProcessor input processor))

(defn get-x [^Input input]
  (.getX input))

(defn get-y [^Input input]
  (.getY input))
