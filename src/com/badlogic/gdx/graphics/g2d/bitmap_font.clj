(ns com.badlogic.gdx.graphics.g2d.bitmap-font
  (:require [com.badlogic.gdx.utils.align :as align])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn data [^BitmapFont font]
  (.getData font))

(defn line-height [^BitmapFont font]
  (.getLineHeight font))

(defn draw! [^BitmapFont font batch text x y target-width align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         (align/k->value align)
         wrap?))
