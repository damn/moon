(ns clojure.gdx.texture-region.get-u2
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn f [region]
  (TextureRegion/.getU2 region))
