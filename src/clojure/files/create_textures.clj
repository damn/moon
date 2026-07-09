(ns clojure.files.create-textures
  (:require [clojure.files :as files]
            [clojure.recursively-search :as recursively-search]
            [clojure.string :as str]
            [clojure.texture :as texture]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (files/internal files folder) extensions))]
             [path (texture/new (files/internal files path))])))
