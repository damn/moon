(ns game.ctx
  (:require [clojure.core-ext :refer [actions!
                                      reduce-actions!]]
            [clojure.math.vector2 :as v]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.keys :as input.keys]
            [game.reaction-txs :as reaction-txs]
            [gdx.graphics.orthographic-camera :as camera]
            [malli.utils :as mu]))

(defn validate [ctx]
  (mu/validate-humanize (:ctx/schema ctx) ctx))

(defn world-viewport-width
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-width world-viewport))

(defn world-viewport-height
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-height world-viewport))

(def zoom-speed 0.025)

(defn visible-tiles [{:keys [ctx/world-viewport]}]
  (camera/visible-tiles (:viewport/camera world-viewport)))

(def pausing? true)

(declare txs-fn-map)

(defn do! [ctx txs]
  (let [handled-txs (try (actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs/fn-map
                     ctx
                     handled-txs)))

(declare draw-fns)

(defn draw! [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))

(defn world-unit-scale [ctx]
  (:ctx/world-unit-scale ctx))

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

(defn sound-names [{:keys [ctx/audio]}]
  (map first audio))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))

(defn player-movement-vector [ctx]
  (let [r (when (key-pressed? ctx input.keys/d) [1  0])
        l (when (key-pressed? ctx input.keys/a) [-1 0])
        u (when (key-pressed? ctx input.keys/w) [0  1])
        d (when (key-pressed? ctx input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v/length v))
          v)))))
