(ns com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create [texture-region]
  (TextureRegionDrawable. ^TextureRegion texture-region))

(defn set-min-size! [^TextureRegionDrawable drawable [w h]]
  (.setMinSize drawable w h))

(defn tint! [^TextureRegionDrawable drawable color]
  (.tint drawable (color/create color)))
