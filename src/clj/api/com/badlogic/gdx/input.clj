(ns clj.api.com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input key))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input key))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input button))

(defn x [^Input input]
  (.getX input))

(defn y [^Input input]
  (.getY input))

(defn set-processor! [^Input input input-processor]
  (.setInputProcessor input input-processor))
