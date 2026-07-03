(ns clojure.gdx.text-tooltip.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextTooltip)))

(defn f [^String tooltip ^Skin skin]
  (TextTooltip. tooltip skin))
