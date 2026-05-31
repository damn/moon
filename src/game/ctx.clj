(ns game.ctx
  (:require [clojure.math.vector2 :as v]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.keys :as input.keys]))

(defn mouse-position [{:keys [ctx/app]}]
  [(input/x (app/input app))
   (input/y (app/input app))])

(defn button-just-pressed? [{:keys [ctx/app]} input-button]
  (input/button-just-pressed? (app/input app) input-button))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v/add player-position
           (v/scale (v/direction player-position world-mouse-position)
                    (min maxrange
                         (v/distance player-position world-mouse-position))))))

(defn player-movement-vector [{:keys [ctx/app]}]
  (let [input (app/input app)
        r (when (input/key-pressed? input input.keys/d) [1  0])
        l (when (input/key-pressed? input input.keys/a) [-1 0])
        u (when (input/key-pressed? input input.keys/w) [0  1])
        d (when (input/key-pressed? input input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))
