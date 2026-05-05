(ns com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn set-initial-time! [initial-time]
  (set! (.initialTime (TooltipManager/getInstance)) initial-time))
