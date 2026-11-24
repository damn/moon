(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager
  "Keeps track of an application's tooltips."
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn instance []
  (TooltipManager/getInstance))

(defn set-initial-time!
  "Seconds from when an actor is hovered to when the tooltip is shown. Default is 2. Call hide all after changing to
  reset internal state."
  [^TooltipManager tooltip-manager t]
  (set! (.initialTime tooltip-manager) t))
