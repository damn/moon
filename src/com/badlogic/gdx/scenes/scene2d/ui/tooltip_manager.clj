(ns com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn getInstance []
  (TooltipManager/getInstance))

(defn setInitialTime [^TooltipManager tooltip-manager initial-time]
  (set! (.initialTime tooltip-manager) initial-time))
