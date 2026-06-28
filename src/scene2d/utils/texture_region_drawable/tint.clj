(ns scene2d.utils.texture-region-drawable.tint
  (:import (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [^TextureRegionDrawable drawable color]
  (.tint drawable color))
