(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn instance []
  (TooltipManager/getInstance))

(defn set-initial-time! [^TooltipManager tooltip-manager t]
  (set! (.initialTime tooltip-manager) t))
