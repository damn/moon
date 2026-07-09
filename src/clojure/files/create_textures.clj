(ns clojure.files.create-textures
  (:require [clojure.files :as files]
            [gdx.file-handle :as file-handle]
            [clojure.string :as str]
            [clojure.texture :as texture]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (file-handle/recursively-search (files/internal files folder) extensions))]
             [path (texture/new (files/internal files path))])))
