(ns scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [texture-region]
  (TextureRegionDrawable. (texture-region/type-hint texture-region)))
