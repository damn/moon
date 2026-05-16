(ns com.badlogic.gdx.application
  (:import (com.badlogic.gdx Application)))

(defn audio [^Application app]
  (.getAudio app))

(defn files [^Application app]
  (.getFiles app))

(defn graphics [^Application app]
  (.getGraphics app))

(defn set-input-processor! [^Application this input-processor]
  (.setInputProcessor (.getInput this) input-processor))

(defn key-pressed? [^Application this key]
  (.isKeyPressed (.getInput this) key))

(defn key-just-pressed? [^Application this key]
  (.isKeyJustPressed (.getInput this) key))

(defn button-just-pressed? [^Application this button]
  (.isButtonJustPressed (.getInput this) button))

(defn mouse-position [^Application this]
  [(.getX (.getInput this))
   (.getY (.getInput this))])
