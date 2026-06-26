(ns scene2d.utils.texture-region-drawable.tint
  (:require [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn f [drawable color]
  (texture-region-drawable/tint drawable color))
