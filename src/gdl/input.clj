(ns gdl.input
  (:import (com.badlogic.gdx Input)))

(defn set-processor! [^Input input input-processor]
  (.setInputProcessor input input-processor))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input key))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input key))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input button))

(defn mouse-position [^Input input]
  [(.getX input)
   (.getY input)])
