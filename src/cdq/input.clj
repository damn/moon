(ns cdq.input
  (:require [moon.input.buttons :as input.buttons]
            [moon.input.keys :as input.keys]
            [clojure.string :as str]
            [clojure.math.vector2 :as v])
  (:import (com.badlogic.gdx Input)))

(defn set-processor! [^Input input input-processor]
  (.setInputProcessor input input-processor))

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
   :zoom-in input.keys/minus
   :zoom-out input.keys/equals
   :unpause-once input.keys/p
   :unpause-continously input.keys/space
   :close-windows-key input.keys/escape
   :toggle-inventory  input.keys/i
   :toggle-entity-info input.keys/e
   :open-debug-button input.buttons/right
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
  (let [r (when (key-pressed? input input.keys/d) [1  0])
        l (when (key-pressed? input input.keys/a) [-1 0])
        u (when (key-pressed? input input.keys/w) [0  1])
        d (when (key-pressed? input input.keys/s) [0 -1])]
    (when (or r l u d)
      (let [v (v/add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))

(defn player-movement-vector [input]
  (WASD-movement-vector input))
