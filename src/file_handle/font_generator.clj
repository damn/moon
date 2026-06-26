(ns file-handle.font-generator
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as font-generator]))

(defn f [file-handle]
  (font-generator/create file-handle))
