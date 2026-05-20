(ns com.badlogic.gdx.scenes.scene2d.ui.skin
  (:require [gdl.files.file-handle :as file-handle]
            [gdl.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(extend-type FileHandle
  file-handle/Skin
  (skin [file-handle]
    (Skin. file-handle)))

(extend-type Skin
  skin/Skin
  (font [skin name]
    (.getFont skin name)))
