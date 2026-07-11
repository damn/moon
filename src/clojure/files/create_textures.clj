(ns clojure.files.create-textures
  (:require [gdx.files :as files]
            [gdx.files.file-handle :as file-handle]
            [gdx.graphics.glutils.file-texture-data :as file-texture-data]
            [gdx.graphics.pixmap :as pixmap]
            [gdx.graphics.texture :as texture]
            [clojure.string :as str]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (file-handle/recursively-search (files/internal files folder) extensions))
                 :let [file (files/internal files path)
                       pixmap (pixmap/new file)]]
             [path (texture/new (file-texture-data/new file
                                                       pixmap
                                                       (pixmap/get-format pixmap)
                                                       false))])))
