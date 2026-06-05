(ns game.ctx.player-movement-vector
  (:require [clojure.math.vector2 :as v]
            [clojure.application :as app]
            [clojure.input :as input]))

(defn player-movement-vector [{:keys [ctx/app]}]
  (let [input (app/input app)
        r (when (input/key-pressed? input :input.keys/d) [1  0])
        l (when (input/key-pressed? input :input.keys/a) [-1 0])
        u (when (input/key-pressed? input :input.keys/w) [0  1])
        d (when (input/key-pressed? input :input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))
