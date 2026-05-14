(ns moon.create.tooltip-config
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [ctx]
  (set! (.initialTime (TooltipManager/getInstance)) 0)
  ctx)
