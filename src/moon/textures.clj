(ns moon.textures
  (:require [gdx.files :as files]
            [gdx.file-handle :as file-handle]
            [gdx.texture-region :as texture-region]
            [gdx.texture-data.file :as file-texture-data]
            [gdx.pixmap :as pixmap]
            [gdx.texture :as texture]
            [clojure.string :as str]))

(defn create
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (file-handle/recursively-search (files/internal files folder) extensions))
                 :let [file (files/internal files path)
                       pixmap (pixmap/new file)]]
             [path (texture/create (file-texture-data/new file
                                                       pixmap
                                                       (pixmap/get-format pixmap)
                                                       false))])))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/create texture x y w h)
      (texture-region/create texture))))
