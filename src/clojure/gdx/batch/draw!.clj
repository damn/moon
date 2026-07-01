(ns clojure.gdx.batch.draw!
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [batch texture verts offset cnt]
  (Batch/.draw batch
               ^Texture texture
               ^floats verts
               (int offset)
               (int cnt)))
