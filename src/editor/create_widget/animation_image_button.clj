(ns editor.create-widget.animation-image-button
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [moon.textures :as textures]))

(defn f [textures image scale]
  (let [texture-region (textures/texture-region textures image)]
    (image-button/new
     (doto (texture-region-drawable/new texture-region)
       (texture-region-drawable/set-min-size! (* scale (texture-region/get-region-width texture-region))
                       (* scale (texture-region/get-region-height texture-region)))))))
