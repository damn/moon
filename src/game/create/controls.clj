(ns game.create.controls
  (:require [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [gdl.input.buttons :as input.buttons]
            [gdl.input.keys :as input.keys]
            [gdl.app :as app]
            [gdl.input :as input]
            [moon.controls :as controls]))

(defn step [ctx]
  (extend-type (class ctx)
    controls/Controls
    (player-movement-vector [{:keys [ctx/app]}]
      (let [input (app/input app)
            r (when (input/key-pressed? input input.keys/d) [1  0])
            l (when (input/key-pressed? input input.keys/a) [-1 0])
            u (when (input/key-pressed? input input.keys/w) [0  1])
            d (when (input/key-pressed? input input.keys/s) [0 -1])]
        (when (or r l u d)
          (let [v (v/normalise (reduce v/add [0 0] (remove nil? [r l u d])))]
            (when (pos? (v/length v))
              v)))))
    )
  (assoc ctx
         :ctx/controls {
                        :zoom-in input.keys/minus
                        :zoom-out input.keys/equals
                        :unpause-once input.keys/p
                        :unpause-continously input.keys/space
                        :close-windows-key input.keys/escape
                        :toggle-inventory  input.keys/i
                        :toggle-entity-info input.keys/e
                        :open-debug-button input.buttons/right
                        }
         :ctx/controls-info (str/join "\n"
                                      ["[W][A][S][D] - Move"
                                       "[ESCAPE] - Close windows"
                                       "[I] - Inventory window"
                                       "[E] - Entity Info window"
                                       "[-]/[=] - Zoom"
                                       "[P]/[SPACE] - Unpause"
                                       "rightclick on tile or entity - open debug data window"
                                       "Leftmouse click - use skill/drop item on cursor"])))
