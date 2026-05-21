(ns clojure.gdx.scenes.scene2d.ui.skin
  (:require [clojure.files.file-handle :as file-handle]
            [clojure.scene2d.ui.skin :as skin])
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
