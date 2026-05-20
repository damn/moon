(ns com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager
  (:require [gdl.app :as app])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(.bindRoot #'app/tooltip-manager-set-initial-time!
           (fn [value]
             (set! (.initialTime (TooltipManager/getInstance)) value)))
