(ns lib.textures
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn- recursively-search [^FileHandle handle extensions]
  (loop [[^FileHandle handle & remaining] (.list handle)
         result []]
    (cond (nil? handle)
          result

          (.isDirectory handle)
          (recur (concat remaining (.list handle)) result)

          (extensions (.extension handle))
          (recur remaining (conj result (.path handle)))

          :else
          (recur remaining result))))

(defn create
  [^Files files {:keys [folder extensions]}]
  (into {}
        (for [path (->> (recursively-search (.internal files folder) extensions)
                        (map #(str/replace-first % folder "")))]
          [path (Texture. (.internal files path))])))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [^Texture texture (get textures file)]
    (if-let [[x y w h] bounds]
      (TextureRegion. texture (int x) (int y) (int w) (int h))
      (TextureRegion. texture))))
