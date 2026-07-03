(ns clojure.gdx.tooltip-manager.set-initial-time
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn f [^TooltipManager tooltip-manager initial-time]
  (set! (.initialTime tooltip-manager) initial-time))
