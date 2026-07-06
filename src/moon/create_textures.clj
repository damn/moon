(ns moon.create-textures
  (:require [clojure.string :as str]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.recursively-search :as recursively-search]
            [clojure.gdx.texture.new-file-handle :as new-texture]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (files/internal files folder) extensions))]
             [path (new-texture/f (files/internal files path))])))
