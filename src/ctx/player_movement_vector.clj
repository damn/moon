(ns ctx.player-movement-vector
  (:require [clojure.add :as add]
            [clojure.length :as length]
            [clojure.normalise :as normalise]
            [gdx.input.key-pressed :as key-pressed?]))

(defn player-movement-vector [{:keys [ctx/input]}]
  (let [r (when (key-pressed?/f input :input.keys/d) [1  0])
        l (when (key-pressed?/f input :input.keys/a) [-1 0])
        u (when (key-pressed?/f input :input.keys/w) [0  1])
        d (when (key-pressed?/f input :input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (normalise/f (reduce add/f [0 0] (remove nil? [r l u d])))]
        (when (pos? (length/f v))
          v)))))
