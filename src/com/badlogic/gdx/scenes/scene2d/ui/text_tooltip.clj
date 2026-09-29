(ns com.badlogic.gdx.scenes.scene2d.ui.text-tooltip
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextTooltip)))

(defn new [^String tooltip ^Skin skin]
  (TextTooltip. tooltip skin))
