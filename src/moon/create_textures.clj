(ns moon.create-textures
  (:require [clojure.string :as str]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.recursively-search :as recursively-search]
            [clojure.gdx.texture.new-file-handle :as new-texture]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (internal/f files folder) extensions))]
             [path (new-texture/f (internal/f files path))])))
