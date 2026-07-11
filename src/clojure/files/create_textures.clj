(ns clojure.files.create-textures
  (:require [com.badlogic.gdx.files :as files]
            [gdl.files.file-handle :as file-handle]
            [com.badlogic.gdx.graphics.glutils.file-texture-data :as file-texture-data]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.texture :as texture]
            [clojure.string :as str]))

; IMPLICIT com.badlogic.gdx.Gdx dependency
; - this is APPLICATION level logic
; - GDX APPLICATION LEVEL -

; - GDX == Static global state - the glue holding libgdx together
; and messing up boundaries

; => For each class in libgdx 'com.badlogic.gdx' namespace or shape-drawer
; see if implicit dependency to Gdx (look at java sources imports)
; => REQUIRE / dispatch on com.badlogic.gdx.Application as first param
; e.g. for texture-loading
; => honest connetions

; ALSO COMPLECTS: texture file-handles ( ! can be as config also with folder extensions PROJECT SPECIFIC)

; AND FOR DEPLOYMENT ANYWAY NEED A DIFFERENT LIST
; AND FILE-HANDLE -> texture pipeline ( WHICH NEEDS GDX GLOBAL STTE )!

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (file-handle/recursively-search (files/internal files folder) extensions))
                 :let [file-handle (files/internal files path)
                       pixmap (pixmap/new file-handle)]]
             [path (texture/new (file-texture-data/new file-handle
                                                       pixmap
                                                       (pixmap/getFormat pixmap)
                                                       false))])))
