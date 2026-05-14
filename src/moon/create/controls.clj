(ns moon.create.controls
  (:require [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [com.badlogic.gdx.input.buttons :as input.buttons]
            [com.badlogic.gdx.input.keys :as input.keys]
            [moon.app :as app]
            [moon.controls :as controls]))

(defn step [ctx]
  (extend-type (class ctx)
    controls/Controls
    (player-movement-vector [{:keys [ctx/app]}]
      (let [r (when (app/key-pressed? app input.keys/d) [1  0])
            l (when (app/key-pressed? app input.keys/a) [-1 0])
            u (when (app/key-pressed? app input.keys/w) [0  1])
            d (when (app/key-pressed? app input.keys/s) [0 -1])]
        (when (or r l u d)
          (let [v (v/add-vs (remove nil? [r l u d]))]
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
