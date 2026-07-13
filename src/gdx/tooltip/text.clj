(ns gdx.tooltip.text
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]))

(defn create [tooltip-text skin]
  (text-tooltip/new tooltip-text skin))
