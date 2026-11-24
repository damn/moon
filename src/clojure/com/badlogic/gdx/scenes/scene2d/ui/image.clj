(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.image
  "Displays a drawable, scaled various way within the widgets bounds. The preferred size is the min size of the drawable.
  Only when using a [[clojure.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable]] will the actor's scale, rotation, and origin be used when drawing."
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create-from-texture
  "Creates an image stretched, and aligned center."
  [^Texture texture]
  (Image. texture))

(defn create-from-drawable
  "Creates an image stretched, and aligned center."
  [^Drawable drawable]
  (Image. drawable))

(defn create-from-texture-region
  "Creates an image stretched, and aligned center."
  [^TextureRegion texture-region]
  (Image. texture-region))

(defn set-drawable!
  "Sets a new drawable for the image. The image's pref size is the drawable's min size. If using the image actor's size rather
  than the pref size, [clojure.com.badlogic.gdx.scenes.scene2d.utils.layout/pack!] can be used to size the image to its pref size.
  
  - `drawable`: May be null."
  [^Image image ^Drawable drawable]
  (.setDrawable image drawable))
