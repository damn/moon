(ns scene2d.utils.texture-region-drawable
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [texture-region]
  (TextureRegionDrawable. ^TextureRegion texture-region))
