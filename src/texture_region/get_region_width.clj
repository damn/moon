(ns texture-region.get-region-width
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn get-region-width [texture-region]
  (texture-region/width texture-region))
