(ns moon.textures
  (:require [gdx.texture-region :as texture-region]
            [gdx.pixmap :as pixmap]
            [gdx.texture :as texture]
            [clojure.string :as str])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Pixmap$Format)
           (com.badlogic.gdx.graphics.glutils FileTextureData)))

(defn- recursively-search [handle extensions]
  (loop [[handle & remaining] (.list ^FileHandle handle)
         result []]
    (cond (nil? handle)
          result

          (.isDirectory ^FileHandle handle)
          (recur (concat remaining (.list ^FileHandle handle)) result)

          (extensions (.extension ^FileHandle handle))
          (recur remaining (conj result (.path ^FileHandle handle)))

          :else
          (recur remaining result))))

(defn create
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search (.internal ^Files files folder) extensions))
                 :let [file (.internal ^Files files path)
                       pixmap (pixmap/new file)]]
             [path (texture/create (FileTextureData. ^FileHandle file
                                                     ^Pixmap pixmap
                                                     ^Pixmap$Format (pixmap/get-format pixmap)
                                                     false))])))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/create texture x y w h)
      (texture-region/create texture))))
