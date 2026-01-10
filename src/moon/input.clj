(ns moon.input
  (:require [clojure.math.vector2 :as v]
            [clojure.string :as str])
  (:import (com.badlogic.gdx Input
                             Input$Buttons
                             Input$Keys)))

(defn key-pressed? [^Input input key]
  (.isKeyPressed input key))

(defn key-just-pressed? [^Input input key]
  (.isKeyJustPressed input key))

(defn button-just-pressed? [^Input input button]
  (.isButtonJustPressed input button))

(defn mouse-position [^Input input]
  [(.getX input)
   (.getY input)])

(def controls
  {
   :zoom-in Input$Keys/MINUS
   :zoom-out Input$Keys/EQUALS
   :unpause-once Input$Keys/P
   :unpause-continously Input$Keys/SPACE
   :close-windows-key Input$Keys/ESCAPE
   :toggle-inventory  Input$Keys/I
   :toggle-entity-info Input$Keys/E
   :open-debug-button Input$Buttons/RIGHT
   }
  )

(def info-text
  (str/join "\n"
            ["[W][A][S][D] - Move"
             "[ESCAPE] - Close windows"
             "[I] - Inventory window"
             "[E] - Entity Info window"
             "[-]/[=] - Zoom"
             "[P]/[SPACE] - Unpause"
             "rightclick on tile or entity - open debug data window"
             "Leftmouse click - use skill/drop item on cursor"]))

(defn- WASD-movement-vector [input]
  (let [r (when (key-pressed? input Input$Keys/D) [1  0])
        l (when (key-pressed? input Input$Keys/A) [-1 0])
        u (when (key-pressed? input Input$Keys/W) [0  1])
        d (when (key-pressed? input Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))

(defn player-movement-vector [input]
  (WASD-movement-vector input))
