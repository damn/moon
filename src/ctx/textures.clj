(ns ctx.textures
  (:require [clojure.string :as str]
            [files.internal :as internal]
            [file-handle.recursively-search :as recursively-search]
            [file-handle.texture :as texture]))

(defn step
  [{:keys [ctx/files]}
   {:keys [folder extensions]}]
  (into {} (for [path (map (fn [path]
                             (str/replace-first path folder ""))
                           (recursively-search/f (internal/f files folder) extensions))]
             [path (texture/f (internal/f files path))])))
