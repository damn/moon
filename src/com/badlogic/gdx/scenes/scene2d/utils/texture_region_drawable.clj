(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [texture-region]
  (TextureRegionDrawable. (texture-region/type-hint texture-region)))

(defn tint [^TextureRegionDrawable drawable color]
  (.tint drawable color))

(defn type-hint
  ^TextureRegionDrawable
  [obj]
  obj)

(def java-class TextureRegionDrawable)
