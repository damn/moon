(ns moon.textures
  (:require [moon.files :as files]
            [moon.file-handle :as file-handle]
            [moon.texture-region :as texture-region]
            [moon.file-texture-data :as file-texture-data]
            [moon.pixmap :as pixmap]
            [moon.texture :as texture]
            [clojure.string :as str]))

(defn create
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

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/new texture x y w h)
      (texture-region/new texture))))
