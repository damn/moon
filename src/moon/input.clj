(ns moon.input
  (:require [clojure.input :as input]
            [clojure.math.vector2 :as v])
  (:import (com.badlogic.gdx Input$Keys)))

(defn key-pressed? [input key]
  (input/key-pressed? input key))

(defn key-just-pressed? [input key]
  (input/key-just-pressed? input key))

(defn button-just-pressed? [input button]
  (input/button-just-pressed? input button))

(defn mouse-position [input]
  (input/mouse-position input))

(defn player-movement-vector [input]
  (let [r (when (input/key-pressed? input Input$Keys/D) [1  0])
        l (when (input/key-pressed? input Input$Keys/A) [-1 0])
        u (when (input/key-pressed? input Input$Keys/W) [0  1])
        d (when (input/key-pressed? input Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))
