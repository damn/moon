(ns gdx.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn get-instance []
  (TooltipManager/getInstance))

(defn set-initial-time! [tooltip-manager initial-time]
  (set! (.initialTime ^TooltipManager tooltip-manager) initial-time))
