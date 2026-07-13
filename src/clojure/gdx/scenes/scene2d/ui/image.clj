(ns clojure.gdx.scenes.scene2d.ui.image
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]))

(defn create [drawable]
  (image/new drawable))

(defn create-from-texture [texture]
  (image/newTexture texture))

(defn create-drawable [drawable]
  (image/newDrawable drawable))

(defn set-drawable! [image drawable]
  (image/setDrawable image drawable))
