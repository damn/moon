(ns gdx.graphics.texture
  (:require [com.badlogic.gdx.graphics.texture :as texture]))

(defn create [texture-data]
  (texture/new texture-data))
