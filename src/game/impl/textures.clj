(ns game.impl.textures
  (:require [clojure.string :as str]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.graphics.texture :as texture]
            [moon.textures])
  (:import (com.badlogic.gdx.graphics Texture)))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [files]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[file & remaining] (file-handle/list (files/internal files folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (file-handle/directory? file)
                                   (recur (concat remaining (file-handle/list file)) result)

                                   (extensions (file-handle/extension file))
                                   (recur remaining (conj result (file-handle/path file)))

                                   :else
                                   (recur remaining result))))]
             [path (Texture. (files/internal files path))])))

(extend-type clojure.lang.PersistentHashMap
  moon.textures/Textures
  (texture-region [textures {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if-let [[x y w h] bounds]
        (texture/region texture x y w h)
        (texture/region texture)))))
