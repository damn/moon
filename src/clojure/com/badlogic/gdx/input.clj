(ns clojure.com.badlogic.gdx.input
  (:import (com.badlogic.gdx Input InputProcessor)))

(defn x [^Input input]
  (.getX input))

(defn y [^Input input]
  (.getY input))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input key))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input key))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input (int button)))

(defn set-input-processor! [^Input input ^InputProcessor processor]
  (.setInputProcessor input processor))
