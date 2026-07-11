(ns clojure.player-movement-vector
  (:require [moon.v2 :as v2]
            [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]))

(defn player-movement-vector [{:keys [ctx/input]}]
  (let [r (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/d)) [1  0])
        l (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/a)) [-1 0])
        u (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/w)) [0  1])
        d (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/s)) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce v2/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))
