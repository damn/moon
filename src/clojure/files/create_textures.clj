(ns clojure.files.create-textures
  (:require [com.badlogic.gdx.files :as files]
            [gdl.files.file-handle :as file-handle]
            [com.badlogic.gdx.graphics.glutils.file-texture-data :as file-texture-data]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.texture :as texture]
            [clojure.string :as str]))

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
