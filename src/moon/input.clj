(ns moon.input
  (:require [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.keys :as input.keys]
            [clojure.math.vector2 :as v]))

(defn key-pressed? [input key]
  (input/key-pressed? input key))

(defn key-just-pressed? [input key]
  (input/key-just-pressed? input key))

(defn button-just-pressed? [input button]
  (input/button-just-pressed? input button))

(defn mouse-position [input]
  (input/mouse-position input))

(defn player-movement-vector [input]
  (let [r (when (input/key-pressed? input input.keys/d) [1  0])
        l (when (input/key-pressed? input input.keys/a) [-1 0])
        u (when (input/key-pressed? input input.keys/w) [0  1])
        d (when (input/key-pressed? input input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))
