(ns bitmap-font.get-line-height
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn f [font]
  (bitmap-font/line-height font))
