(ns com.badlogic.gdx.scenes.scene2d.ui.image
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)))

(def java-class Image)

(defn create [arg]
  (Image. arg))

(defn create-from-texture [texture]
  (Image. (texture/type-hint texture)))

(defn create-from-texture-region [texture-region]
  (Image. (texture-region/type-hint texture-region)))

(defn create-from-drawable [drawable]
  (Image. drawable))

(defn set-drawable! [^Image image drawable]
  (.setDrawable image drawable))
