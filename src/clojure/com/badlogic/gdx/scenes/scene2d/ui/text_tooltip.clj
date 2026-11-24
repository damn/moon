(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip
  "A tooltip that shows a label."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextTooltip)))

(defn create [^String text ^Skin skin]
  (TextTooltip. text skin))
