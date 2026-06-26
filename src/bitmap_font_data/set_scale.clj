(ns bitmap-font-data.set-scale
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font.bitmap-font-data :as bitmap-font-data]))

(defn f [data scale]
  (bitmap-font-data/set-scale! data scale))
