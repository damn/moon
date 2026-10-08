(ns editor.ui.widget.image
  (:require [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn scaled-image-button [texture-region scale]
  (ImageButton.
   (doto (TextureRegionDrawable. ^TextureRegion texture-region)
     (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                  (* scale (.getRegionHeight ^TextureRegion texture-region))))))

(defn create [state image]
  (scaled-image-button (textures/texture-region (:textures @state) image) 2))
