(ns clojure.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create [texture-region]
  (texture-region-drawable/new texture-region))

(defn set-min-size! [texture-region-drawable min-w min-h]
  (texture-region-drawable/setMinSize texture-region-drawable
                                      min-w
                                      min-h))

(defn tint! [texture-region-drawable color]
  (texture-region-drawable/tint texture-region-drawable color))
