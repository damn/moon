(ns clojure.gdx.shape-drawer.new
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^Batch batch ^TextureRegion texture-region]
  (ShapeDrawer. batch texture-region))
