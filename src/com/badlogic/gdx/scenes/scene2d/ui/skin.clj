(ns com.badlogic.gdx.scenes.scene2d.ui.skin
  (:require [com.badlogic.gdx.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create [file-handle]
  (Skin. (file-handle/type-hint file-handle)))

(defn create-from-skin [skin]
  (Skin. skin))

(defn get-font [^Skin skin name]
  (.getFont skin name))

(defn type-hint
  ^Skin
  [obj]
  obj)
