(ns game.ctx.player-movement-vector
  (:require [clojure.math.vector2 :as v]
            [clojure.math.vector2.add :as add]
            [clojure.math.vector2.length :as length]
            [clojure.math.vector2.normalise :as normalise]
            [com.badlogic.gdx.input :as input]))

(defn player-movement-vector [{:keys [ctx/input]}]
  (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
        l (when (input/key-pressed? input :input.keys/a) [-1 0])
        u (when (input/key-pressed? input :input.keys/w) [0  1])
        d (when (input/key-pressed? input :input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (normalise/f (reduce add/f [0 0] (remove nil? [r l u d])))]
        (when (pos? (length/f v))
          v)))))
