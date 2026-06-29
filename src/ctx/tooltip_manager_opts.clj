(ns ctx.tooltip-manager-opts
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [_ctx]
  (set! (.initialTime (TooltipManager/getInstance)) 0))
