(ns gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as drawable]))

(defn create
  [{:keys [drawable/texture-region
           drawable/size
           drawable/tint]}]
  (let [drawable (doto (drawable/create texture-region)
                   (drawable/set-min-size! size size))]
    (when tint
      (drawable/tint! drawable (color/create tint)))
    drawable))

(defn create*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (drawable/create texture-region)
    (drawable/set-min-size! (* scale (texture-region/width texture-region))
                            (* scale (texture-region/height texture-region)))))
