(ns clojure.freetype-font-generator
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font
  [^FreeTypeFontGenerator generator
   {:keys [size
           min-filter
           mag-filter]}]
  (.generateFont generator (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                             (set! (.size params) size)
                             (set! (.minFilter params) min-filter)
                             (set! (.magFilter params) mag-filter)
                             params)))

(defn dispose! [^FreeTypeFontGenerator generator]
  (.dispose generator))
