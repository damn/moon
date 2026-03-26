(ns moon.create.input
  (:require [clojure.string :as str]
            [moon.input :as input]
            [moon.vector2 :as v])
  (:import (com.badlogic.gdx Input
                             Input$Buttons
                             Input$Keys)))

(defn- WASD-movement-vector [input]
  (let [r (when (input/key-pressed? input Input$Keys/D) [1  0])
        l (when (input/key-pressed? input Input$Keys/A) [-1 0])
        u (when (input/key-pressed? input Input$Keys/W) [0  1])
        d (when (input/key-pressed? input Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))

(defn step [ctx]
  (assoc ctx
         :ctx/controls {
                        :zoom-in Input$Keys/MINUS
                        :zoom-out Input$Keys/EQUALS
                        :unpause-once Input$Keys/P
                        :unpause-continously Input$Keys/SPACE
                        :close-windows-key Input$Keys/ESCAPE
                        :toggle-inventory  Input$Keys/I
                        :toggle-entity-info Input$Keys/E
                        :open-debug-button Input$Buttons/RIGHT
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
    (.isKeyPressed input key))

  (key-just-pressed? [input key]
    (.isKeyJustPressed input key))

  (button-just-pressed? [input button]
    (.isButtonJustPressed input button))

  (mouse-position [input]
    [(.getX input)
     (.getY input)])

  (player-movement-vector [input]
    (WASD-movement-vector input)))
