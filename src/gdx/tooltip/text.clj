(ns gdx.tooltip.text
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextTooltip)))

(defn create [tooltip-text skin]
  (TextTooltip. ^String tooltip-text ^Skin skin))
