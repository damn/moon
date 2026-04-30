(ns clojure.input
  (:import (com.badlogic.gdx Input)))

(defn key-pressed? [this key]
  (.isKeyPressed this key))

(defn key-just-pressed? [this key]
  (.isKeyJustPressed this key))

(defn button-just-pressed? [this button]
  (.isButtonJustPressed this button))

(defn mouse-position [this]
  [(.getX this)
   (.getY this)])

(defn set-processor! [this input-processor]
  (.setInputProcessor this input-processor))
