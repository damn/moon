(ns clojure.create-textures
  (:require [clojure.texture :as texture]
            [clojure.string :as str]
            [clojure.files :as files]
            [clojure.recursively-search :as recursively-search]))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (files/internal files folder) extensions))]
             [path (texture/new (files/internal files path))])))
