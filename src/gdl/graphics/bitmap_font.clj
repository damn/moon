(ns gdl.graphics.bitmap-font
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font.data :as data]
            [clj.api.com.badlogic.gdx.utils.align :as align]
            [clojure.string :as str]))

(defn scale-x [font]
  (data/scale-x (font/data font)))

(defn set-scale! [font scale]
  (data/set-scale! (font/data font) scale))

(defn enable-markup! [font enable?]
  (data/enable-markup! (font/data font) enable?))

(defn use-integer-positions! [font use-integer-positions?]
  (font/use-integer-positions! font use-integer-positions?))

(defn draw! [font batch text x y target-width h-align wrap?]
  (font/draw! font
              batch
              text
              x
              y
              target-width
              (align/k->value h-align)
              wrap?))

(defn text-height [font text]
  (-> text
      (str/split #"\n")
      count
      (* (font/line-height font))))
