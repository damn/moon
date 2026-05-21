(ns clojure.gdx.graphics.texture
  (:require [clojure.files.file-handle :as file-handle]
            [clojure.graphics.texture :as texture])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(extend-type FileHandle
  file-handle/Texture
  (texture [file-handle]
    (Texture. file-handle)))

(extend-type Texture
  texture/Texture
  (region
    ([texture]
     (TextureRegion. texture))
    ([texture x y w h]
     (TextureRegion. texture (int x) (int y) (int w) (int h)))))
