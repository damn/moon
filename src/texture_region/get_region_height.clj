(ns texture-region.get-region-height
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn get-region-height [texture-region]
  (texture-region/height texture-region))
