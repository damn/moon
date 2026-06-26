(ns scene2d.ui.image
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]))

(defmulti create class)

(defmethod create texture/java-class [texture]
  (image/create-from-texture texture))

(defmethod create texture-region/java-class [texture-region]
  (image/create-from-texture-region texture-region))

(defmethod create texture-region-drawable/java-class [drawable]
  (image/create-from-drawable drawable))

(comment
 (defn f [drawable scaling align]
   (Image. drawable scaling align))
 )
