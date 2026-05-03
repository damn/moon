(ns clojure.input
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [^Input this key]
  (.isKeyPressed this key))

(defn key-just-pressed? [^Input this key]
  (.isKeyJustPressed this key))

(defn button-just-pressed? [^Input this button]
  (.isButtonJustPressed this button))

(defn mouse-position [^Input this]
  [(.getX this)
   (.getY this)])
