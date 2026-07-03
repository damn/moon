(ns clojure.gdx.tooltip-manager.get-instance
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn f []
  (TooltipManager/getInstance))
