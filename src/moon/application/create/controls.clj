(ns moon.application.create.controls
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Input$Buttons
                             Input$Keys)))

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
                                       "Leftmouse click - use skill/drop item on cursor"])))
