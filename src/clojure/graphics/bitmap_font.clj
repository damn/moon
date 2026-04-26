(ns clojure.graphics.bitmap-font
  (:require [clojure.gdx.utils.align :as align]
            [clojure.string :as str])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)))

(defn scale-x [^BitmapFont font]
  (.scaleX (.getData font)))

(defn set-scale! [^BitmapFont font scale]
  (.setScale (.getData font) scale))

(defn enable-markup! [^BitmapFont font enable?]
  (set! (.markupEnabled (.getData font)) enable?))

(defn use-integer-positions! [^BitmapFont font use-integer-positions?]
  (.setUseIntegerPositions font use-integer-positions?))

(defn draw! [^BitmapFont font batch text x y target-width h-align wrap?]
  (.draw font
         batch
         text
         (float x)
         (float y)
         (float target-width)
         (align/k->value h-align)
         wrap?))

(defn text-height [^BitmapFont font text]
  (-> text
      (str/split #"\n")
      count
      (* (.getLineHeight font))))
