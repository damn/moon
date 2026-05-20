(ns com.badlogic.gdx.scenes.scene2d.ui.text-tooltip
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defmethod actor/create-listener
  :listener/text-tooltip
  [[_ [tooltip skin]]]
  (TextTooltip. ^String tooltip ^Skin skin))
