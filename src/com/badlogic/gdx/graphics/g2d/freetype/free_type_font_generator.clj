(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  (:require [com.badlogic.gdx.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. (file-handle/type-hint file-handle)))

(defn generate-font [^FreeTypeFontGenerator generator parameter]
  (.generateFont generator parameter))

(defn type-hint
  ^FreeTypeFontGenerator
  [obj]
  obj)
