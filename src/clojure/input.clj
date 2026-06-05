(ns clojure.input
  (:import (com.badlogic.gdx Input
                             Input$Buttons)))

(defn set-processor! [^Input input processor]
  (.setInputProcessor input processor))

(defn key-pressed? [^Input input k]
  (.isKeyPressed input k))

(defn key-just-pressed? [^Input input k]
  (.isKeyJustPressed input k))

(defn x [^Input input]
  (.getX input))

(defn y [^Input input]
  (.getY input))

(defn button-just-pressed? [^Input input k]
  (.isButtonJustPressed input (case k
                                :input.buttons/left  Input$Buttons/LEFT
                                :input.buttons/right Input$Buttons/RIGHT)))
