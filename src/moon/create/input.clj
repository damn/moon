(ns moon.create.input
  (:require [gdl.input :as gdx-input]
            [clj.api.com.badlogic.gdx.input.buttons :as input.buttons]
            [clj.api.com.badlogic.gdx.input.keys :as input.keys]
            [clojure.string :as str]
            [moon.input :as input]
            [moon.vector2 :as v])
  (:import (com.badlogic.gdx Input)))

(defn step [ctx]
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
                                       "Leftmouse click - use skill/drop item on cursor"])
         )
  )

(extend-type Input
  input/Input
  (key-pressed? [input key]
    (gdx-input/key-pressed? input key))

  (key-just-pressed? [input key]
    (gdx-input/key-just-pressed? input (if (keyword? key)
                                         (case key
                                           :input.keys/enter input.keys/enter)
                                         key)))

  (button-just-pressed? [input button]
    (gdx-input/button-just-pressed? input button))

  (mouse-position [input]
    [(gdx-input/x input)
     (gdx-input/y input)])

  (player-movement-vector [input]
    (let [r (when (input/key-pressed? input input.keys/d) [1  0])
          l (when (input/key-pressed? input input.keys/a) [-1 0])
          u (when (input/key-pressed? input input.keys/w) [0  1])
          d (when (input/key-pressed? input input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))
