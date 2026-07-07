(ns clojure.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn get-instance []
  (TooltipManager/getInstance))

(defn set-initial-time! [^TooltipManager tooltip-manager initial-time]
  (set! (.initialTime tooltip-manager) initial-time))
