(ns clojure.scenes.scene2d.ui.text-tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defn create [tooltip skin]
  (TextTooltip. ^String tooltip ^Skin skin))
