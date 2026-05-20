(ns com.badlogic.gdx.textures
  (:require [clojure.string :as str]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics.texture :as texture]
            [gdl.textures]))

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
             [path (file-handle/texture (files/internal files path))])))

(extend-type clojure.lang.PersistentHashMap
  gdl.textures/Textures
  (texture-region [textures {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if-let [[x y w h] bounds]
        (texture/region texture x y w h)
        (texture/region texture)))))
