(ns moon.create-textures
  (:require
            [com.badlogic.gdx.graphics.texture :as texture] [clojure.string :as str]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.recursively-search :as recursively-search]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (files/internal files folder) extensions))]
             [path (texture/new-file-handle (files/internal files path))])))
