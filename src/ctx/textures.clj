(ns ctx.textures
  (:require [clojure.string :as str]
            [files.internal :as internal]
            [file-handle.recursively-search :as recursively-search])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Texture)))

(defn step
  [{:keys [ctx/files]}
   {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (internal/f files folder) extensions))]
             [path (Texture. ^FileHandle (internal/f files path))])))
