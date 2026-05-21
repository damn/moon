(ns com.badlogic.gdx.scenes.scene2d.ui.text-tooltip
  (:require [gdl.scene2d.listener :as listener])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defmethod listener/create
  :listener/text-tooltip
  [[_ [tooltip skin]]]
  (TextTooltip. ^String tooltip ^Skin skin))
