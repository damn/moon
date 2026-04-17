(ns clojure.gdx.scene2d.ui.text-tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defn create [^String text ^Skin skin]
  (TextTooltip. text skin))
