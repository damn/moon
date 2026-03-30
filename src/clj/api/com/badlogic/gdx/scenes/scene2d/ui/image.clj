(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.image
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defmulti create class)

(defmethod create Drawable [^Drawable drawable]
  (Image. drawable))

(defmethod create Texture [^Texture texture]
  (Image. texture))
