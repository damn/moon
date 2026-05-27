(ns game.impl.textures
  (:require [clojure.string :as str]
            [moon.textures])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [^Files files]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[^FileHandle file & remaining] (.list (.internal files folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (.isDirectory file)
                                   (recur (concat remaining (.list file)) result)

                                   (extensions (.extension file))
                                   (recur remaining (conj result (.path file)))

                                   :else
                                   (recur remaining result))))]
             [path (Texture. (.internal files path))])))

(extend-type clojure.lang.PersistentHashMap
  moon.textures/Textures
  (texture-region [textures {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if-let [[x y w h] bounds]
        (TextureRegion. texture (int x) (int y) (int w) (int h))
        (TextureRegion. texture)))))
