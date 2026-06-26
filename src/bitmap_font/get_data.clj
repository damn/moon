(ns bitmap-font.get-data
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn get-data [font]
  (bitmap-font/data font))
