(ns gdl.ui.tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextTooltip)))

(defn create [text skin]
  (TextTooltip. (str text) skin))
