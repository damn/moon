(ns moon.create.tooltip-config
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [ctx {:keys [initial-time]}]
  (set! (.initialTime (TooltipManager/getInstance)) initial-time)
  ctx)
