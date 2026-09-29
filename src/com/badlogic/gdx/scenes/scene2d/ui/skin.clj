(ns com.badlogic.gdx.scenes.scene2d.ui.skin
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn new [file-handle]
  (Skin. ^FileHandle file-handle))

(defn getFont [skin font-name]
  (.getFont ^Skin skin font-name))
