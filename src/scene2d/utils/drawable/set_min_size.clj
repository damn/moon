(ns scene2d.utils.drawable.set-min-size
  (:require [com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]))

(defn f [drawable min-width min-height]
  (drawable/set-min-size! drawable min-width min-height))
