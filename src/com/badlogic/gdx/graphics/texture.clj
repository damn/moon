(ns com.badlogic.gdx.graphics.texture
  (:require [com.badlogic.gdx.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.graphics Texture)))

(defn create [file-handle]
  (Texture. (file-handle/type-hint file-handle)))

(defn type-hint
  ^Texture
  [obj]
  obj)
