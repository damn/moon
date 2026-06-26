(ns com.badlogic.gdx.graphics.texture
  (:require [com.badlogic.gdx.files.file-handle :as file-handle]
            [com.badlogic.gdx.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx.graphics Texture)))

(def java-class Texture)

(defn create [file-handle]
  (Texture. (file-handle/type-hint file-handle)))

(defn create-from-pixmap [pixmap]
  (Texture. (pixmap/type-hint pixmap)))

(defn type-hint
  ^Texture
  [obj]
  obj)
