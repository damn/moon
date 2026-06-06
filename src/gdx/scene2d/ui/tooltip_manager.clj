(ns gdx.scene2d.ui.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn set-initial-time! [value]
  (set! (.initialTime (TooltipManager/getInstance)) value))
