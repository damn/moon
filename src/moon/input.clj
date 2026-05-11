(ns moon.input
  (:require [com.badlogic.gdx.input.keys :as input.keys]
            [clojure.math.vector2 :as v])
  (:import (com.badlogic.gdx Input)))

(def set-processor! Input/.setInputProcessor)

(defn key-pressed? [^Input this key]
  (.isKeyPressed this key))

(defn key-just-pressed? [^Input this key]
  (.isKeyJustPressed this key))

(defn button-just-pressed? [^Input this button]
  (.isButtonJustPressed this button))

(defn mouse-position [^Input this]
  [(.getX this)
   (.getY this)])

(defn player-movement-vector [input]
  (let [r (when (key-pressed? input input.keys/d) [1  0])
        l (when (key-pressed? input input.keys/a) [-1 0])
        u (when (key-pressed? input input.keys/w) [0  1])
        d (when (key-pressed? input input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))
