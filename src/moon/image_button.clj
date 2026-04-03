(ns moon.image-button
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create
  [{:keys [drawable/texture-region
           drawable/scale
           skin]}]
  (let [scale (or scale 1)]
    (image-button/create (doto (texture-region-drawable/create texture-region)
                           (drawable/set-min-size! (* scale (texture-region/width texture-region))
                                                   (* scale (texture-region/height texture-region)))))))
