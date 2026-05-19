(ns game.impl.textures
  (:require [gdl.app :as app]
            [gdl.files :as files]
            [gdl.files.file-handle :as file]
            [clojure.string :as str]
            [com.badlogic.gdx.gdx :as gdx]
            [gdl.graphics.texture :as texture]
            [moon.textures]))

(def folder "resources/")
(def extensions #{"png" "bmp"})

(defn create
  [{:keys [ctx/app]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (loop [[file & remaining] (file/list (files/internal (app/files app) folder))
                                  result []]
                             (cond (nil? file)
                                   result

                                   (file/directory? file)
                                   (recur (concat remaining (file/list file)) result)

                                   (extensions (file/extension file))
                                   (recur remaining (conj result (file/path file)))

                                   :else
                                   (recur remaining result))))]
             [path (gdx/texture path)])))

(extend-type clojure.lang.PersistentHashMap
  moon.textures/Textures
  (texture-region [textures {:keys [image/file image/bounds]}]
    (assert file)
    (assert (contains? textures file))
    (let [texture (get textures file)]
      (if-let [[x y w h] bounds]
        (texture/region texture x y w h)
        (texture/region texture)))))
