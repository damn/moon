(ns clojure.player-movement-vector
  (:require [clojure.v2.add :as add]
            [clojure.v2.length :as length]
            [clojure.v2.normalise :as normalise]
            [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]))

(defn player-movement-vector [{:keys [ctx/input]}]
  (let [r (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/d)) [1  0])
        l (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/a)) [-1 0])
        u (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/w)) [0  1])
        d (when (input/isKeyPressed input (input-keys/key-to-value :input.keys/s)) [0 -1])]
    (when (or r l u d)
      (let [v (normalise/f (reduce add/f [0 0] (remove nil? [r l u d])))]
        (when (pos? (length/f v))
          v)))))
