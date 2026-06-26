(ns com.badlogic.gdx.scenes.scene2d.ui.text-tooltip
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextTooltip)))

(defn create [tooltip skin]
  (TextTooltip. ^String tooltip (skin/type-hint skin)))
