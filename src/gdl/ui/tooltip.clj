(ns gdl.ui.tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defn create [text ^Skin skin]
  (TextTooltip. (str text) skin))
