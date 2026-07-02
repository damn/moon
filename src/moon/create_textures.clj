(ns moon.create-textures
  (:require [clojure.string :as str]
            [clojure.gdx.recursively-search :as recursively-search])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(defn f
  [files {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (Files/.internal files folder) extensions))]
             [path (Texture. ^FileHandle (Files/.internal files path))])))
