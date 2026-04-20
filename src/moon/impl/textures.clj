(ns moon.impl.textures
  (:require [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.string :as str]))

(defn create [{:keys [ctx/files]} {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (file-handle/recursively-search (files/internal files folder) extensions))]
             [path (texture/create path)])))
